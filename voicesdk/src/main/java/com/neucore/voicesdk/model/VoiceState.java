package com.neucore.voicesdk.model;

/**
 * 链路状态。
 */
public enum VoiceState {
    /** 空闲 */
    IDLE,
    /** 录音中（等待说话/说话中） */
    LISTENING,
    /** 识别中 */
    RECOGNIZING,
    /** 意图解析中 */
    PARSING,
    /** 指令执行中 */
    EXECUTING,
    /** 播报中 */
    SPEAKING,
    /** 本轮完成 */
    DONE,
    /** 出错 */
    ERROR
}
