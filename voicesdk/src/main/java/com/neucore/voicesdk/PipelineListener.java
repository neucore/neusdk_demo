package com.neucore.voicesdk;

import com.neucore.voicesdk.model.AsrResult;
import com.neucore.voicesdk.model.CommandResult;
import com.neucore.voicesdk.model.VoiceIntent;
import com.neucore.voicesdk.model.VoiceState;
import com.neucore.voicesdk.model.VoiceTurn;

/**
 * 语音链路回调。所有回调都在主线程触发，可直接更新 UI。
 */
public interface PipelineListener {

    /**
     * 链路状态变化。
     */
    void onStateChanged(VoiceState state);

    /**
     * 语音 -> 文本 完成。
     */
    void onSpeechRecognized(AsrResult result);

    /**
     * 文本 -> 意图 完成。
     */
    void onIntentParsed(VoiceIntent intent);

    /**
     * 指令执行完成。
     */
    void onCommandExecuted(CommandResult result);

    /**
     * 回复文本生成完成（即将播报）。
     */
    void onReplyReady(String replyText);

    /**
     * 本轮结束（成功、失败都会回调）。
     */
    void onCompleted(VoiceTurn turn);

    /**
     * 出错。
     *
     * @param stage 阶段，取值见 {@link VoicePipeline#STAGE_RECORD} 等常量
     */
    void onError(int stage, String message, Throwable error);
}
