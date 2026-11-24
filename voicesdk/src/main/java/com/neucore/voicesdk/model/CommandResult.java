package com.neucore.voicesdk.model;

/**
 * 指令执行结果。
 */
public final class CommandResult {

    private final boolean success;
    private final String speechText;
    private final String message;
    private final String data;

    public CommandResult(boolean success, String speechText, String message, String data) {
        this.success = success;
        this.speechText = speechText;
        this.message = message;
        this.data = data;
    }

    public static CommandResult ok(String speechText) {
        return new CommandResult(true, speechText, "ok", null);
    }

    public static CommandResult ok(String speechText, String data) {
        return new CommandResult(true, speechText, "ok", data);
    }

    public static CommandResult failed(String message) {
        return new CommandResult(false, null, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    /**
     * 设备侧自定义播报文本；为 null 时由 ReplyComposer 按模板生成。
     */
    public String getSpeechText() {
        return speechText;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 附加数据（一般是 JSON 字符串），供页面展示或上报。
     */
    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return "CommandResult{success=" + success + ", message='" + message + "', data=" + data + "}";
    }
}
