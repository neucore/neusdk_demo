package com.neucore.voicesdk.api;

import com.neucore.voicesdk.model.CommandResult;
import com.neucore.voicesdk.model.VoiceIntent;

/**
 * 具体设备处理器。由宿主按业务注册到 {@link CommandExecutor}。
 * <p>
 * 注册示例：
 * <pre>
 * DefaultCommandExecutor executor = new DefaultCommandExecutor();
 * executor.register("turn_on", "light", new DeviceHandler() {
 *     public CommandResult handle(VoiceIntent intent) {
 *         // 调用真实设备接口
 *         return CommandResult.ok("好的，已打开客厅的灯");
 *     }
 * });
 * </pre>
 */
public interface DeviceHandler {

    CommandResult handle(VoiceIntent intent);
}
