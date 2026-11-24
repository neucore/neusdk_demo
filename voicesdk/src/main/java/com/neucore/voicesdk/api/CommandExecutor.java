package com.neucore.voicesdk.api;

import android.content.Context;

import com.neucore.voicesdk.VoiceSdkConfig;
import com.neucore.voicesdk.model.CommandResult;
import com.neucore.voicesdk.model.VoiceIntent;

/**
 * 指令执行：把意图落到真实设备（灯、空调、门锁……）。
 * <p>
 * 默认实现：{@link com.neucore.voicesdk.impl.DefaultCommandExecutor}，
 * 支持通过 {@link com.neucore.voicesdk.api.DeviceHandler} 注册具体设备处理器；
 * 未注册时返回执行成功（仅记录日志），便于先跑通链路。
 */
public interface CommandExecutor {

    void init(Context context, VoiceSdkConfig config) throws Exception;

    CommandResult execute(VoiceIntent intent);

    void release();
}
