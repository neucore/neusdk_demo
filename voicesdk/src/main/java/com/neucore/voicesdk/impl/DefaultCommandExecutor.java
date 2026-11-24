package com.neucore.voicesdk.impl;

import android.content.Context;

import com.neucore.voicesdk.VoiceSdkConfig;
import com.neucore.voicesdk.api.CommandExecutor;
import com.neucore.voicesdk.api.DeviceHandler;
import com.neucore.voicesdk.internal.VoiceLog;
import com.neucore.voicesdk.model.CommandResult;
import com.neucore.voicesdk.model.VoiceIntent;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认指令执行器：按 action/device 查找宿主注册的 {@link DeviceHandler}。
 * <p>
 * 查找顺序：<code>action|device</code> -> <code>action|*</code> -> <code>*|*</code>。
 * 未注册时视为执行成功（仅记录日志），便于先跑通"语音->语音"闭环，再逐个接入真实设备。
 */
public class DefaultCommandExecutor implements CommandExecutor {

    private final Map<String, DeviceHandler> handlers = new HashMap<String, DeviceHandler>();
    private DomainConfig domain;

    @Override
    public void init(Context context, VoiceSdkConfig config) {
        domain = DomainConfig.create(config.getDomainJson());
    }

    /**
     * 注册处理器。
     *
     * @param action 动作，如 turn_on；传 "*" 表示任意动作
     * @param device 设备，如 light；传 "*" 表示任意设备
     */
    public DefaultCommandExecutor register(String action, String device, DeviceHandler handler) {
        handlers.put(key(action, device), handler);
        return this;
    }

    public DefaultCommandExecutor register(String action, DeviceHandler handler) {
        return register(action, "*", handler);
    }

    public DefaultCommandExecutor unregister(String action, String device) {
        handlers.remove(key(action, device));
        return this;
    }

    private static String key(String action, String device) {
        return (action == null ? "*" : action) + "|" + (device == null ? "*" : device);
    }

    @Override
    public CommandResult execute(VoiceIntent intent) {
        if (intent == null || intent.getAction() == null) {
            return CommandResult.failed("未识别到可执行动作");
        }
        String action = intent.getAction();
        String device = intent.getDevice();

        DeviceHandler handler = handlers.get(key(action, device));
        if (handler == null) {
            handler = handlers.get(key(action, "*"));
        }
        if (handler == null) {
            handler = handlers.get(key("*", "*"));
        }

        if (handler != null) {
            try {
                CommandResult r = handler.handle(intent);
                if (r != null) {
                    VoiceLog.d("handler 命中 " + key(action, device) + " -> " + r);
                    return r;
                }
            } catch (Exception e) {
                VoiceLog.e("设备处理器异常：" + key(action, device), e);
                return CommandResult.failed("设备执行异常：" + e.getMessage());
            }
        }

        VoiceLog.d("未注册处理器，按成功处理：" + key(action, device));
        return new CommandResult(true, null, "ok(no-handler)", intent.toJson().toString());
    }

    public DomainConfig getDomain() {
        return domain;
    }

    @Override
    public void release() {
        handlers.clear();
    }
}
