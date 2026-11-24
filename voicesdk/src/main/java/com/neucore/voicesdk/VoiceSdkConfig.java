package com.neucore.voicesdk;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * SDK 配置。既可用 Builder 编程式构造，也可从 assets / 文件里的 JSON 加载。
 * <p>
 * JSON 示例（放在宿主 App 的 assets/voice_sdk.json）：
 * <pre>
 * {
 *   "language": "zh",
 *   "sampleRate": 16000,
 *   "vad": { "frameMs": 100, "silenceRms": 0.012, "silenceMs": 1000, "maxSeconds": 10 },
 *   "endpoints": { "asr": "http://127.0.0.1:8700", "tts": "http://127.0.0.1:8700" },
 *   "engines": {
 *     "asr":     "com.neucore.voicesdk.impl.HttpAsrEngine",
 *     "intent":  "com.neucore.voicesdk.impl.RuleIntentParser",
 *     "command": "com.neucore.voicesdk.impl.DefaultCommandExecutor",
 *     "reply":   "com.neucore.voicesdk.impl.TemplateReplyComposer",
 *     "tts":     "com.neucore.voicesdk.impl.AndroidTtsEngine"
 *   },
 *   "domainAsset": "voice_domain.json",
 *   "debug": true
 * }
 * </pre>
 * 个性化实现只需把 engines.* 换成自己实现的类名，或运行时注入实例。
 */
public final class VoiceSdkConfig {

    public static final String DEFAULT_ASR_ENGINE = "com.neucore.voicesdk.impl.HttpAsrEngine";
    public static final String DEFAULT_INTENT_PARSER = "com.neucore.voicesdk.impl.RuleIntentParser";
    public static final String DEFAULT_COMMAND_EXECUTOR = "com.neucore.voicesdk.impl.DefaultCommandExecutor";
    public static final String DEFAULT_REPLY_COMPOSER = "com.neucore.voicesdk.impl.TemplateReplyComposer";
    public static final String DEFAULT_TTS_ENGINE = "com.neucore.voicesdk.impl.AndroidTtsEngine";

    private final int sampleRate;
    private final int frameMs;
    private final float silenceRms;
    private final int silenceMs;
    private final int maxRecordSeconds;

    private final String language;
    private final String asrEndpoint;
    private final String ttsEndpoint;

    private final String asrEngineClass;
    private final String intentParserClass;
    private final String commandExecutorClass;
    private final String replyComposerClass;
    private final String ttsEngineClass;

    private final String domainJson;
    private final boolean debug;

    private VoiceSdkConfig(Builder b) {
        this.sampleRate = b.sampleRate;
        this.frameMs = b.frameMs;
        this.silenceRms = b.silenceRms;
        this.silenceMs = b.silenceMs;
        this.maxRecordSeconds = b.maxRecordSeconds;
        this.language = b.language;
        this.asrEndpoint = b.asrEndpoint;
        this.ttsEndpoint = b.ttsEndpoint;
        this.asrEngineClass = b.asrEngineClass;
        this.intentParserClass = b.intentParserClass;
        this.commandExecutorClass = b.commandExecutorClass;
        this.replyComposerClass = b.replyComposerClass;
        this.ttsEngineClass = b.ttsEngineClass;
        this.domainJson = b.domainJson;
        this.debug = b.debug;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public int getFrameMs() {
        return frameMs;
    }

    public float getSilenceRms() {
        return silenceRms;
    }

    public int getSilenceMs() {
        return silenceMs;
    }

    public int getMaxRecordSeconds() {
        return maxRecordSeconds;
    }

    public String getLanguage() {
        return language;
    }

    public String getAsrEndpoint() {
        return asrEndpoint;
    }

    public String getTtsEndpoint() {
        return ttsEndpoint;
    }

    public String getAsrEngineClass() {
        return asrEngineClass;
    }

    public String getIntentParserClass() {
        return intentParserClass;
    }

    public String getCommandExecutorClass() {
        return commandExecutorClass;
    }

    public String getReplyComposerClass() {
        return replyComposerClass;
    }

    public String getTtsEngineClass() {
        return ttsEngineClass;
    }

    /**
     * 自定义域定义（JSON 字符串），null 表示使用 SDK 内置域。
     */
    public String getDomainJson() {
        return domainJson;
    }

    public boolean isDebug() {
        return debug;
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 从 assets 读取 JSON 配置。文件不存在时返回默认配置。
     */
    public static VoiceSdkConfig fromAsset(Context context, String assetPath) {
        try {
            InputStream in = context.getAssets().open(assetPath);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int n;
            while ((n = in.read(buf)) > 0) {
                out.write(buf, 0, n);
            }
            in.close();
            return fromJson(out.toString("UTF-8"), context);
        } catch (IOException e) {
            return builder().build();
        }
    }

    /**
     * 解析 JSON 配置。context 可为 null；不为 null 时会尝试加载 domainAsset。
     */
    public static VoiceSdkConfig fromJson(String json, Context context) {
        Builder b = builder();
        if (TextUtils.isEmpty(json)) {
            return b.build();
        }
        try {
            JSONObject o = new JSONObject(json);
            if (o.has("language")) {
                b.language(o.optString("language", "zh"));
            }
            if (o.has("sampleRate")) {
                b.sampleRate(o.optInt("sampleRate", 16000));
            }
            if (o.has("debug")) {
                b.debug(o.optBoolean("debug", false));
            }
            JSONObject vad = o.optJSONObject("vad");
            if (vad != null) {
                b.frameMs(vad.optInt("frameMs", 100));
                b.silenceRms((float) vad.optDouble("silenceRms", 0.012));
                b.silenceMs(vad.optInt("silenceMs", 1000));
                b.maxRecordSeconds(vad.optInt("maxSeconds", 10));
            }
            JSONObject endpoints = o.optJSONObject("endpoints");
            if (endpoints != null) {
                b.asrEndpoint(endpoints.optString("asr", b.asrEndpoint));
                b.ttsEndpoint(endpoints.optString("tts", b.ttsEndpoint));
            }
            if (o.has("asrEndpoint")) {
                b.asrEndpoint(o.optString("asrEndpoint", b.asrEndpoint));
            }
            if (o.has("ttsEndpoint")) {
                b.ttsEndpoint(o.optString("ttsEndpoint", b.ttsEndpoint));
            }
            JSONObject engines = o.optJSONObject("engines");
            if (engines != null) {
                b.asrEngineClass(engines.optString("asr", b.asrEngineClass));
                b.intentParserClass(engines.optString("intent", b.intentParserClass));
                b.commandExecutorClass(engines.optString("command", b.commandExecutorClass));
                b.replyComposerClass(engines.optString("reply", b.replyComposerClass));
                b.ttsEngineClass(engines.optString("tts", b.ttsEngineClass));
            }
            if (o.has("domainJson")) {
                b.domainJson(o.optString("domainJson", null));
            } else if (o.has("domainAsset") && context != null) {
                b.domainJson(readAsset(context, o.optString("domainAsset")));
            }
        } catch (Exception ignored) {
            // 配置非法时退回默认值，不影响初始化
        }
        return b.build();
    }

    private static String readAsset(Context context, String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        try {
            InputStream in = context.getAssets().open(path);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int n;
            while ((n = in.read(buf)) > 0) {
                out.write(buf, 0, n);
            }
            in.close();
            return out.toString("UTF-8");
        } catch (IOException e) {
            return null;
        }
    }

    public static final class Builder {
        private int sampleRate = 16000;
        private int frameMs = 100;
        private float silenceRms = 0.012f;
        private int silenceMs = 1000;
        private int maxRecordSeconds = 10;

        private String language = "zh";
        private String asrEndpoint = "http://127.0.0.1:8700";
        private String ttsEndpoint = "http://127.0.0.1:8700";

        private String asrEngineClass = DEFAULT_ASR_ENGINE;
        private String intentParserClass = DEFAULT_INTENT_PARSER;
        private String commandExecutorClass = DEFAULT_COMMAND_EXECUTOR;
        private String replyComposerClass = DEFAULT_REPLY_COMPOSER;
        private String ttsEngineClass = DEFAULT_TTS_ENGINE;

        private String domainJson;
        private boolean debug = false;

        public Builder() {
        }

        Builder(VoiceSdkConfig c) {
            this.sampleRate = c.sampleRate;
            this.frameMs = c.frameMs;
            this.silenceRms = c.silenceRms;
            this.silenceMs = c.silenceMs;
            this.maxRecordSeconds = c.maxRecordSeconds;
            this.language = c.language;
            this.asrEndpoint = c.asrEndpoint;
            this.ttsEndpoint = c.ttsEndpoint;
            this.asrEngineClass = c.asrEngineClass;
            this.intentParserClass = c.intentParserClass;
            this.commandExecutorClass = c.commandExecutorClass;
            this.replyComposerClass = c.replyComposerClass;
            this.ttsEngineClass = c.ttsEngineClass;
            this.domainJson = c.domainJson;
            this.debug = c.debug;
        }

        public Builder sampleRate(int v) {
            this.sampleRate = v;
            return this;
        }

        public Builder frameMs(int v) {
            this.frameMs = v;
            return this;
        }

        public Builder silenceRms(float v) {
            this.silenceRms = v;
            return this;
        }

        public Builder silenceMs(int v) {
            this.silenceMs = v;
            return this;
        }

        public Builder maxRecordSeconds(int v) {
            this.maxRecordSeconds = v;
            return this;
        }

        public Builder language(String v) {
            this.language = v;
            return this;
        }

        public Builder asrEndpoint(String v) {
            this.asrEndpoint = v;
            return this;
        }

        public Builder ttsEndpoint(String v) {
            this.ttsEndpoint = v;
            return this;
        }

        public Builder asrEngineClass(String v) {
            this.asrEngineClass = v;
            return this;
        }

        public Builder intentParserClass(String v) {
            this.intentParserClass = v;
            return this;
        }

        public Builder commandExecutorClass(String v) {
            this.commandExecutorClass = v;
            return this;
        }

        public Builder replyComposerClass(String v) {
            this.replyComposerClass = v;
            return this;
        }

        public Builder ttsEngineClass(String v) {
            this.ttsEngineClass = v;
            return this;
        }

        public Builder domainJson(String v) {
            this.domainJson = v;
            return this;
        }

        public Builder debug(boolean v) {
            this.debug = v;
            return this;
        }

        public VoiceSdkConfig build() {
            return new VoiceSdkConfig(this);
        }
    }
}
