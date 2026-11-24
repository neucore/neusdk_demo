package com.neucore.voicesdk.impl;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import com.neucore.voicesdk.VoiceSdkConfig;
import com.neucore.voicesdk.api.TtsCallback;
import com.neucore.voicesdk.api.TtsEngine;
import com.neucore.voicesdk.internal.VoiceLog;
import com.neucore.voicesdk.model.VoiceIntent;

import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 默认 TTS 实现：Android 系统 TextToSpeech（离线，无需模型与网络）。
 * <p>
 * 注意：部分定制终端可能未内置中文 TTS 引擎，此时 init 会抛异常，
 * 可改用 {@link HttpTtsEngine}（服务端返回音频流）。
 */
public class AndroidTtsEngine implements TtsEngine {

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private TextToSpeech tts;
    private volatile boolean ready;
    private volatile TtsCallback currentCallback;
    private String currentUtteranceId;

    @Override
    public void init(Context context, VoiceSdkConfig config) throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] ok = new boolean[]{false};
        final String[] error = new String[]{null};

        tts = new TextToSpeech(context.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    ok[0] = true;
                } else {
                    error[0] = "TextToSpeech 初始化失败，status=" + status;
                }
                latch.countDown();
            }
        });

        if (!latch.await(8, TimeUnit.SECONDS)) {
            throw new Exception("TextToSpeech 初始化超时");
        }
        if (!ok[0]) {
            throw new Exception(error[0] == null ? "TextToSpeech 不可用" : error[0]);
        }

        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                VoiceLog.d("TTS onStart " + utteranceId);
            }

            @Override
            public void onDone(String utteranceId) {
                notifyDone(utteranceId);
            }

            @Override
            public void onError(String utteranceId) {
                notifyError(utteranceId, "TTS 播报失败");
            }
        });
        ready = true;
        VoiceLog.d("AndroidTtsEngine 就绪");
    }

    @Override
    public void speak(String text, String language, VoiceIntent intent, TtsCallback callback) {
        if (!ready || tts == null) {
            if (callback != null) {
                callback.onError("TTS 未就绪");
            }
            return;
        }
        if (text == null || text.trim().isEmpty()) {
            if (callback != null) {
                callback.onDone();
            }
            return;
        }

        applyLanguage(language);
        currentCallback = callback;
        currentUtteranceId = "vsdk-" + System.currentTimeMillis();
        int ret;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ret = tts.speak(text, TextToSpeech.QUEUE_FLUSH, (Bundle) null, currentUtteranceId);
        } else {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, currentUtteranceId);
            ret = tts.speak(text, TextToSpeech.QUEUE_FLUSH, params);
        }
        if (ret != TextToSpeech.SUCCESS) {
            notifyError(currentUtteranceId, "TTS speak 调用失败，ret=" + ret);
        }
    }

    private void applyLanguage(String language) {
        Locale locale = "en".equals(language) ? Locale.US : Locale.SIMPLIFIED_CHINESE;
        try {
            int available = tts.isLanguageAvailable(locale);
            if (available >= TextToSpeech.LANG_AVAILABLE) {
                tts.setLanguage(locale);
            } else {
                VoiceLog.w("系统 TTS 不支持 " + locale + "，使用默认语言");
            }
        } catch (Exception e) {
            VoiceLog.w("设置 TTS 语言失败：" + e.getMessage());
        }
    }

    private void notifyDone(String utteranceId) {
        if (currentUtteranceId != null && !currentUtteranceId.equals(utteranceId)) {
            return;
        }
        final TtsCallback cb = currentCallback;
        currentCallback = null;
        if (cb != null) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    cb.onDone();
                }
            });
        }
    }

    private void notifyError(String utteranceId, final String message) {
        if (currentUtteranceId != null && utteranceId != null && !currentUtteranceId.equals(utteranceId)) {
            return;
        }
        final TtsCallback cb = currentCallback;
        currentCallback = null;
        if (cb != null) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    cb.onError(message);
                }
            });
        }
    }

    @Override
    public void stop() {
        if (tts != null) {
            try {
                tts.stop();
            } catch (Exception ignored) {
            }
        }
        currentCallback = null;
    }

    @Override
    public void release() {
        ready = false;
        currentCallback = null;
        if (tts != null) {
            try {
                tts.stop();
                tts.shutdown();
            } catch (Exception ignored) {
            }
            tts = null;
        }
    }
}
