package com.neucore.voicesdk;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.neucore.voicesdk.api.AsrEngine;
import com.neucore.voicesdk.api.CommandExecutor;
import com.neucore.voicesdk.api.IntentParser;
import com.neucore.voicesdk.api.ReplyComposer;
import com.neucore.voicesdk.api.TtsCallback;
import com.neucore.voicesdk.api.TtsEngine;
import com.neucore.voicesdk.internal.AudioRecorder;
import com.neucore.voicesdk.internal.EngineFactory;
import com.neucore.voicesdk.internal.VoiceLog;
import com.neucore.voicesdk.model.AsrResult;
import com.neucore.voicesdk.model.CommandResult;
import com.neucore.voicesdk.model.VoiceIntent;
import com.neucore.voicesdk.model.VoiceState;
import com.neucore.voicesdk.model.VoiceTurn;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 语音链路编排：语音 -> 文本 -> 意图 -> 指令执行 -> 文本 -> 语音。
 * <p>
 * 录音、识别、解析、执行、播报全部在单一后台线程串行执行，回调切回主线程。
 * 每个环节都可以通过 setter 或配置类名替换为自定义实现。
 */
public class VoicePipeline {

    public static final int STAGE_RECORD = 1;
    public static final int STAGE_ASR = 2;
    public static final int STAGE_INTENT = 3;
    public static final int STAGE_EXECUTE = 4;
    public static final int STAGE_REPLY = 5;
    public static final int STAGE_TTS = 6;

    private final Context context;
    private final VoiceSdkConfig config;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService worker = Executors.newSingleThreadExecutor();
    private final AtomicBoolean busy = new AtomicBoolean(false);

    private volatile PipelineListener listener;
    private volatile boolean cancelled;

    private AsrEngine asrEngine;
    private IntentParser intentParser;
    private CommandExecutor commandExecutor;
    private ReplyComposer replyComposer;
    private TtsEngine ttsEngine;

    private boolean asrReady;
    private boolean intentReady;
    private boolean executorReady;
    private boolean composerReady;
    private boolean ttsReady;

    public VoicePipeline(Context context, VoiceSdkConfig config) {
        this.context = context.getApplicationContext();
        this.config = config;
    }

    public VoiceSdkConfig getConfig() {
        return config;
    }

    public VoicePipeline setListener(PipelineListener listener) {
        this.listener = listener;
        return this;
    }

    // ---------------- 可替换环节 ----------------

    public VoicePipeline setAsrEngine(AsrEngine engine) {
        this.asrEngine = engine;
        this.asrReady = false;
        return this;
    }

    public VoicePipeline setIntentParser(IntentParser parser) {
        this.intentParser = parser;
        this.intentReady = false;
        return this;
    }

    public VoicePipeline setCommandExecutor(CommandExecutor executor) {
        this.commandExecutor = executor;
        this.executorReady = false;
        return this;
    }

    public VoicePipeline setReplyComposer(ReplyComposer composer) {
        this.replyComposer = composer;
        this.composerReady = false;
        return this;
    }

    public VoicePipeline setTtsEngine(TtsEngine engine) {
        this.ttsEngine = engine;
        this.ttsReady = false;
        return this;
    }

    public AsrEngine getAsrEngine() {
        return asrEngine;
    }

    public CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    public TtsEngine getTtsEngine() {
        return ttsEngine;
    }

    // ---------------- 入口 ----------------

    /**
     * 录音并执行完整链路。需已授予 RECORD_AUDIO 权限。
     */
    public void start() {
        if (!busy.compareAndSet(false, true)) {
            notifyError(STAGE_RECORD, "上一轮尚未结束", null);
            return;
        }
        cancelled = false;
        worker.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    runRecordTurn();
                } finally {
                    busy.set(false);
                }
            }
        });
    }

    /**
     * 文本模式：跳过录音与 ASR，用于在没有麦克风/ASR 服务时验证后半段链路。
     */
    public void startText(final String text) {
        if (!busy.compareAndSet(false, true)) {
            notifyError(STAGE_INTENT, "上一轮尚未结束", null);
            return;
        }
        cancelled = false;
        worker.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    long t0 = System.currentTimeMillis();
                    AsrResult asr = new AsrResult(text, detectLanguage(text), 1f);
                    notifySpeechRecognized(asr);
                    runFromText(asr, 0, t0);
                } finally {
                    busy.set(false);
                }
            }
        });
    }

    /**
     * 直接送入 PCM（外部录音/AEC 后音频）。
     */
    public void startPcm(final short[] pcm, final int sampleRate) {
        if (!busy.compareAndSet(false, true)) {
            notifyError(STAGE_ASR, "上一轮尚未结束", null);
            return;
        }
        cancelled = false;
        worker.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    long t0 = System.currentTimeMillis();
                    ensureEngines();
                    setState(VoiceState.RECOGNIZING);
                    long a = System.currentTimeMillis();
                    AsrResult asr = asrEngine.transcribe(pcm, sampleRate);
                    long asrMs = System.currentTimeMillis() - a;
                    notifySpeechRecognized(asr);
                    runFromText(asr, asrMs, t0);
                } catch (Exception e) {
                    fail(STAGE_ASR, "语音识别失败：" + messageOf(e), e);
                } finally {
                    busy.set(false);
                }
            }
        });
    }

    public void cancel() {
        cancelled = true;
        TtsEngine tts = ttsEngine;
        if (tts != null) {
            try {
                tts.stop();
            } catch (Exception ignored) {
            }
        }
    }

    public boolean isBusy() {
        return busy.get();
    }

    /**
     * 预热引擎（加载模型等）。建议在页面 onCreate 里调用一次。
     */
    public void prepareAsync() {
        worker.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ensureEngines();
                    VoiceLog.i("引擎预热完成");
                } catch (Exception e) {
                    VoiceLog.e("引擎预热失败", e);
                    notifyError(STAGE_ASR, "初始化失败：" + messageOf(e), e);
                }
            }
        });
    }

    public void release() {
        cancel();
        worker.execute(new Runnable() {
            @Override
            public void run() {
                releaseEngines();
            }
        });
        worker.shutdown();
        try {
            worker.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        }
    }

    // ---------------- 内部流程 ----------------

    private void runRecordTurn() {
        long t0 = System.currentTimeMillis();
        try {
            ensureEngines();
            setState(VoiceState.LISTENING);
            AudioRecorder.Result rec = AudioRecorder.record(config, new AudioRecorder.CancelChecker() {
                @Override
                public boolean isCancelled() {
                    return cancelled;
                }
            });
            if (cancelled) {
                setState(VoiceState.IDLE);
                return;
            }
            if (rec.isEmpty()) {
                fail(STAGE_RECORD, "没有检测到有效语音", null);
                return;
            }

            setState(VoiceState.RECOGNIZING);
            long a = System.currentTimeMillis();
            AsrResult asr = asrEngine.transcribe(rec.pcm, rec.sampleRate);
            long asrMs = System.currentTimeMillis() - a;
            notifySpeechRecognized(asr);

            if (asr == null || asr.isEmpty()) {
                fail(STAGE_ASR, "未识别到文字", null);
                return;
            }
            if (cancelled) {
                setState(VoiceState.IDLE);
                return;
            }
            runFromText(asr, asrMs, t0);
        } catch (SecurityException e) {
            fail(STAGE_RECORD, "缺少录音权限（RECORD_AUDIO）", e);
        } catch (Exception e) {
            fail(STAGE_RECORD, "录音失败：" + messageOf(e), e);
        }
    }

    private void runFromText(AsrResult asr, long asrMs, long turnStart) {
        VoiceTurn turn = new VoiceTurn();
        turn.setAsr(asr);
        turn.setAsrMs(asrMs);

        String language = TextUtils.isEmpty(asr.getLanguage())
                ? config.getLanguage() : asr.getLanguage();

        try {
            setState(VoiceState.PARSING);
            long p0 = System.currentTimeMillis();
            VoiceIntent intent = intentParser.parse(asr.getText(), language);
            turn.setIntent(intent);
            turn.setIntentMs(System.currentTimeMillis() - p0);
            notifyIntentParsed(intent);
            voiceLogIntent(intent);

            if (cancelled) {
                setState(VoiceState.IDLE);
                return;
            }

            setState(VoiceState.EXECUTING);
            long e0 = System.currentTimeMillis();
            CommandResult command = commandExecutor.execute(intent);
            turn.setCommand(command);
            turn.setExecuteMs(System.currentTimeMillis() - e0);
            notifyCommandExecuted(command);

            if (cancelled) {
                setState(VoiceState.IDLE);
                return;
            }

            setState(VoiceState.SPEAKING);
            long r0 = System.currentTimeMillis();
            String reply = replyComposer.compose(intent, command, language);
            if (TextUtils.isEmpty(reply)) {
                reply = "en".equals(language) ? "Sorry, I didn't catch that." : "抱歉，我没听懂";
            }
            turn.setReplyText(reply);
            notifyReplyReady(reply);
            VoiceLog.d("回复：" + reply + "（话术耗时 " + (System.currentTimeMillis() - r0) + "ms）");

            speakAndWait(reply, language, intent, turn);

            turn.setTotalMs(System.currentTimeMillis() - turnStart);
            setState(VoiceState.DONE);
            notifyCompleted(turn);
        } catch (Exception e) {
            turn.setTotalMs(System.currentTimeMillis() - turnStart);
            turn.setError(messageOf(e));
            VoiceLog.w("处理失败：" + messageOf(e));
            setState(VoiceState.ERROR);
            notifyError(STAGE_INTENT, "处理失败：" + messageOf(e), e);
            notifyCompleted(turn);
        }
    }

    private void speakAndWait(String reply, String language, VoiceIntent intent, VoiceTurn turn) {
        final CountDownLatch latch = new CountDownLatch(1);
        final String[] error = new String[]{null};
        TtsEngine tts = ttsEngine;
        if (tts == null) {
            return;
        }
        try {
            tts.speak(reply, language, intent, new TtsCallback() {
                @Override
                public void onDone() {
                    latch.countDown();
                }

                @Override
                public void onError(String message) {
                    error[0] = message;
                    latch.countDown();
                }
            });
        } catch (Exception e) {
            error[0] = messageOf(e);
            latch.countDown();
        }

        // 兜底超时，避免 TTS 不回调导致链路卡死
        long timeout = Math.max(5000, Math.min(60000, reply.length() * 400L));
        try {
            latch.await(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignored) {
        }
        if (error[0] != null) {
            turn.setError(error[0]);
            VoiceLog.w("播报失败：" + error[0]);
            notifyError(STAGE_TTS, error[0], null);
        }
    }

    private void ensureEngines() throws Exception {
        synchronized (this) {
            if (asrEngine == null) {
                asrEngine = EngineFactory.createAsr(context, config);
            }
            if (!asrReady) {
                long t = System.currentTimeMillis();
                asrEngine.init(context, config);
                asrReady = true;
                VoiceLog.i("AsrEngine 就绪（" + (System.currentTimeMillis() - t) + "ms）");
            }
            if (intentParser == null) {
                intentParser = EngineFactory.createIntentParser(context, config);
            }
            if (!intentReady) {
                intentParser.init(context, config);
                intentReady = true;
            }
            if (commandExecutor == null) {
                commandExecutor = EngineFactory.createCommandExecutor(context, config);
            }
            if (!executorReady) {
                commandExecutor.init(context, config);
                executorReady = true;
            }
            if (replyComposer == null) {
                replyComposer = EngineFactory.createReplyComposer(context, config);
            }
            if (!composerReady) {
                replyComposer.init(context, config);
                composerReady = true;
            }
            if (ttsEngine == null) {
                ttsEngine = EngineFactory.createTts(context, config);
            }
            if (!ttsReady) {
                try {
                    ttsEngine.init(context, config);
                    ttsReady = true;
                } catch (Exception e) {
                    VoiceLog.e("TTS 初始化失败，本轮仅输出文本", e);
                    ttsReady = false;
                }
            }
        }
    }

    private synchronized void releaseEngines() {
        try {
            if (asrEngine != null) {
                asrEngine.release();
            }
        } catch (Exception ignored) {
        }
        try {
            if (ttsEngine != null) {
                ttsEngine.release();
            }
        } catch (Exception ignored) {
        }
        try {
            if (intentParser != null) {
                intentParser.release();
            }
        } catch (Exception ignored) {
        }
        try {
            if (commandExecutor != null) {
                commandExecutor.release();
            }
        } catch (Exception ignored) {
        }
        try {
            if (replyComposer != null) {
                replyComposer.release();
            }
        } catch (Exception ignored) {
        }
        asrReady = false;
        intentReady = false;
        executorReady = false;
        composerReady = false;
        ttsReady = false;
    }

    // ---------------- 回调 ----------------

    private void setState(final VoiceState state) {
        final PipelineListener l = listener;
        if (l == null) {
            return;
        }
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                l.onStateChanged(state);
            }
        });
    }

    private void notifySpeechRecognized(final AsrResult result) {
        final PipelineListener l = listener;
        if (l == null) {
            return;
        }
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                l.onSpeechRecognized(result);
            }
        });
    }

    private void notifyIntentParsed(final VoiceIntent intent) {
        final PipelineListener l = listener;
        if (l == null) {
            return;
        }
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                l.onIntentParsed(intent);
            }
        });
    }

    private void notifyCommandExecuted(final CommandResult result) {
        final PipelineListener l = listener;
        if (l == null) {
            return;
        }
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                l.onCommandExecuted(result);
            }
        });
    }

    private void notifyReplyReady(final String reply) {
        final PipelineListener l = listener;
        if (l == null) {
            return;
        }
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                l.onReplyReady(reply);
            }
        });
    }

    private void notifyCompleted(final VoiceTurn turn) {
        final PipelineListener l = listener;
        if (l == null) {
            return;
        }
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                l.onCompleted(turn);
            }
        });
    }

    private void notifyError(final int stage, final String message, final Throwable error) {
        final PipelineListener l = listener;
        if (l == null) {
            return;
        }
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                l.onError(stage, message, error);
            }
        });
    }

    private void fail(int stage, String message, Throwable error) {
        VoiceLog.w("链路失败[" + stage + "] " + message);
        setState(VoiceState.ERROR);
        notifyError(stage, message, error);
        VoiceTurn turn = new VoiceTurn();
        turn.setError(message);
        notifyCompleted(turn);
    }

    private static String detectLanguage(String text) {
        if (text != null) {
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (c >= '\u4e00' && c <= '\u9fa5') {
                    return "zh";
                }
            }
        }
        return "en";
    }

    private static String messageOf(Throwable t) {
        if (t == null) {
            return "未知错误";
        }
        String m = t.getMessage();
        return m == null ? t.getClass().getSimpleName() : m;
    }

    private static void voiceLogIntent(VoiceIntent intent) {
        VoiceLog.d("意图：" + intent.toJson().toString());
    }
}
