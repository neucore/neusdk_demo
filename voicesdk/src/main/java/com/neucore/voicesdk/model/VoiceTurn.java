package com.neucore.voicesdk.model;

/**
 * 一次完整交互的结果（语音 -> 文本 -> 意图 -> 执行 -> 文本 -> 语音）。
 */
public final class VoiceTurn {

    private AsrResult asr;
    private VoiceIntent intent;
    private CommandResult command;
    private String replyText;
    private long asrMs;
    private long intentMs;
    private long executeMs;
    private long totalMs;
    private String error;

    public AsrResult getAsr() {
        return asr;
    }

    public void setAsr(AsrResult asr) {
        this.asr = asr;
    }

    public VoiceIntent getIntent() {
        return intent;
    }

    public void setIntent(VoiceIntent intent) {
        this.intent = intent;
    }

    public CommandResult getCommand() {
        return command;
    }

    public void setCommand(CommandResult command) {
        this.command = command;
    }

    public String getReplyText() {
        return replyText;
    }

    public void setReplyText(String replyText) {
        this.replyText = replyText;
    }

    public long getAsrMs() {
        return asrMs;
    }

    public void setAsrMs(long asrMs) {
        this.asrMs = asrMs;
    }

    public long getIntentMs() {
        return intentMs;
    }

    public void setIntentMs(long intentMs) {
        this.intentMs = intentMs;
    }

    public long getExecuteMs() {
        return executeMs;
    }

    public void setExecuteMs(long executeMs) {
        this.executeMs = executeMs;
    }

    public long getTotalMs() {
        return totalMs;
    }

    public void setTotalMs(long totalMs) {
        this.totalMs = totalMs;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "VoiceTurn{asr=" + asr + ", intent=" + intent + ", command=" + command
                + ", replyText='" + replyText + "', totalMs=" + totalMs + "}";
    }
}
