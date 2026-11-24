package com.neucore.voicesdk;

import android.content.Context;

import com.neucore.voicesdk.internal.VoiceLog;
import com.neucore.voicesdk.model.VoiceState;

/**
 * SDK 门面。
 * <pre>
 * VoiceSdkConfig config = VoiceSdkConfig.builder()
 *         .asrEndpoint("http://192.168.1.10:8700")
 *         .build();
 * VoiceSdk.init(context, config);
 *
 * VoicePipeline pipeline = VoiceSdk.get().newPipeline();
 * pipeline.setListener(listener);
 * pipeline.start();
 * </pre>
 */
public final class VoiceSdk {

    private static volatile VoiceSdk instance;

    private final Context appContext;
    private volatile VoiceSdkConfig config;

    private VoiceSdk(Context context, VoiceSdkConfig config) {
        this.appContext = context.getApplicationContext();
        this.config = config;
        VoiceLog.setDebug(config.isDebug());
    }

    public static VoiceSdk init(Context context, VoiceSdkConfig config) {
        if (context == null) {
            throw new IllegalArgumentException("context == null");
        }
        VoiceSdk sdk = new VoiceSdk(context, config == null ? VoiceSdkConfig.builder().build() : config);
        instance = sdk;
        VoiceLog.i("VoiceSdk 初始化完成，ASR=" + sdk.config.getAsrEndpoint()
                + "，language=" + sdk.config.getLanguage());
        return sdk;
    }

    public static VoiceSdk get() {
        VoiceSdk sdk = instance;
        if (sdk == null) {
            throw new IllegalStateException("请先调用 VoiceSdk.init(context, config)");
        }
        return sdk;
    }

    public static boolean isInitialized() {
        return instance != null;
    }

    public Context getContext() {
        return appContext;
    }

    public VoiceSdkConfig getConfig() {
        return config;
    }

    /**
     * 使用当前配置创建一条新链路。
     */
    public VoicePipeline newPipeline() {
        return new VoicePipeline(appContext, config);
    }

    public VoicePipeline newPipeline(PipelineListener listener) {
        VoicePipeline p = newPipeline();
        p.setListener(listener);
        return p;
    }

    /**
     * 更新配置（例如用户在界面上改了服务地址）。
     */
    public void updateConfig(VoiceSdkConfig newConfig) {
        if (newConfig != null) {
            this.config = newConfig;
            VoiceLog.setDebug(newConfig.isDebug());
        }
    }

    /**
     * 释放全部资源。之后可重新 init。
     */
    public void shutdown() {
        instance = null;
        VoiceLog.i("VoiceSdk 已关闭");
    }

    /**
     * 当前状态枚举的字符串，便于日志展示。
     */
    public static String stateName(VoiceState state) {
        return state == null ? "UNKNOWN" : state.name();
    }
}
