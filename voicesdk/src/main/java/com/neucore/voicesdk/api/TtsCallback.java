package com.neucore.voicesdk.api;

/**
 * TTS 回调。所有回调均在主线程触发。
 */
public interface TtsCallback {

    void onDone();

    void onError(String message);
}
