package com.neucore.voicesdk.api;

import android.content.Context;

import com.neucore.voicesdk.VoiceSdkConfig;
import com.neucore.voicesdk.model.AsrResult;

/**
 * 语音识别引擎：PCM(16bit/单声道) -> 文本。
 * <p>
 * 默认实现：{@link com.neucore.voicesdk.impl.HttpAsrEngine}（调用 HTTP ASR 服务）。
 * 自定义：实现本接口并在配置中指定类名，或运行时通过
 * {@link com.neucore.voicesdk.VoicePipeline#setAsrEngine(AsrEngine)} 注入。
 */
public interface AsrEngine {

    /**
     * 初始化。可能耗时（加载模型），不应在主线程调用。
     */
    void init(Context context, VoiceSdkConfig config) throws Exception;

    /**
     * 识别一段语音。
     *
     * @param pcm        16bit 小端 PCM
     * @param sampleRate 采样率，默认 16000
     */
    AsrResult transcribe(short[] pcm, int sampleRate) throws Exception;

    /**
     * 释放资源。
     */
    void release();
}
