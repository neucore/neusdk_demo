package com.neucore.voicesdk.api;

import android.content.Context;

import com.neucore.voicesdk.VoiceSdkConfig;
import com.neucore.voicesdk.model.VoiceIntent;

/**
 * 语音合成/播报：文本 -> 声音。
 * <p>
 * 默认实现：{@link com.neucore.voicesdk.impl.AndroidTtsEngine}（系统 TTS，离线）。
 * 可选实现：{@link com.neucore.voicesdk.impl.HttpTtsEngine}（服务端返回音频流）。
 */
public interface TtsEngine {

    void init(Context context, VoiceSdkConfig config) throws Exception;

    /**
     * 播报文本。实现应在播报结束后回调 {@link TtsCallback#onDone()}，
     * 失败时回调 {@link TtsCallback#onError(String)}。
     *
     * @param intent 当前意图，便于按意图选择预合成音库（可为 null）
     */
    void speak(String text, String language, VoiceIntent intent, TtsCallback callback);

    void stop();

    void release();
}
