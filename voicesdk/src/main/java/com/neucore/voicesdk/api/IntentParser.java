package com.neucore.voicesdk.api;

import android.content.Context;

import com.neucore.voicesdk.VoiceSdkConfig;
import com.neucore.voicesdk.model.VoiceIntent;

/**
 * 意图解析：文本 -> 结构化指令。
 * <p>
 * 默认实现：{@link com.neucore.voicesdk.impl.RuleIntentParser}（纯规则，离线，毫秒级）。
 * 自定义：实现本接口并配置类名，例如接入微调模型或云端 NLU。
 */
public interface IntentParser {

    void init(Context context, VoiceSdkConfig config) throws Exception;

    /**
     * @param text     识别文本
     * @param language zh / en
     */
    VoiceIntent parse(String text, String language);

    void release();
}
