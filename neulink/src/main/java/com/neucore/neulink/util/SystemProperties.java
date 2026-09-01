package com.neucore.neulink.util;

import com.neucore.neulink.log.NeuLogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import cn.hutool.core.util.ObjectUtil;

public class SystemProperties {
    private static final String GETPROP_EXECUTABLE_PATH = "/system/bin/getprop";
    private static final String SETPROP_EXECUTABLE_PATH = "/system/bin/setprop";
    private static final String TAG = "SystemProperties";

    /**
     * 读取全部系统属性
     * @return Properties，不会返回null；读取异常返回空Properties
     */
    public static Properties get() {
        Process process = null;
        BufferedReader bufferedReader = null;
        Properties properties = new Properties();
        try {
            process = new ProcessBuilder()
                    .command(GETPROP_EXECUTABLE_PATH)
                    .redirectErrorStream(true)
                    .start();
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            // 修复：循环内读取，不再丢失第一行
            while ((line = bufferedReader.readLine()) != null) {
                if (ObjectUtil.isEmpty(line)) {
                    continue;
                }
                // 格式校验：必须 [xxx]: [yyy]
                if (!line.startsWith("[") || !line.contains("]: [")) {
                    continue;
                }
                // 去掉开头 [ 和结尾 ]
                String content = line.substring(1);
                if (content.endsWith("]")) {
                    content = content.substring(0, content.length() - 1);
                }
                String[] propArr = content.split("\\]: \\[", 2); // 限制分割2份，value内包含分隔符不会被切分
                if (propArr.length >= 2) {
                    String key = propArr[0];
                    String value = propArr[1];
                    properties.setProperty(key, value);
                }
            }
            // 等待进程结束
            process.waitFor();
        } catch (Exception e) {
            NeuLogUtils.eTag(TAG, "Failed to read System Property ", e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ignored) {
                }
            }
            if (process != null) {
                process.destroy();
                try {
                    process.waitFor(); // 等待进程回收，避免僵尸进程
                } catch (InterruptedException ignored) {
                }
            }
        }
        return properties;
    }

    /**
     * 设置系统属性 setprop
     * @param key prop key
     * @param value prop value
     * @return true=命令执行完成；不代表属性一定生效（权限不足会静默失败）
     */
    public static boolean set(String key, String value) {
        Process process = null;
        BufferedReader bufferedReader = null;
        boolean success = false;
        try {
            if (ObjectUtil.isEmpty(value)) {
                value = "";
            }
            process = new ProcessBuilder()
                    .command(SETPROP_EXECUTABLE_PATH, key, value)
                    .redirectErrorStream(true)
                    .start();
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            // 消费全部输出
            while (bufferedReader.readLine() != null) {
                // no-op
            }
            int exitCode = process.waitFor();
            success = exitCode == 0;
            if (!success) {
                NeuLogUtils.wTag(TAG, String.format("setprop exitCode=%d, key=%s, value=%s", exitCode, key, value));
            }
        } catch (Exception e) {
            NeuLogUtils.eTag(TAG, "Failed to set System Property key:" + key, e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ignored) {
                }
            }
            if (process != null) {
                process.destroy();
                try {
                    process.waitFor();
                } catch (InterruptedException ignored) {
                }
            }
        }
        return success;
    }

    // 简易封装：获取单个属性，不用读取全部prop，更高效
    public static String getProp(String key) {
        Properties props = get();
        return props.getProperty(key, "");
    }
}
