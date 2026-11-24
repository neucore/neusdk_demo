package com.neucore.voicesdk.api;

import android.content.Context;

import com.neucore.voicesdk.VoiceSdkConfig;
import com.neucore.voicesdk.model.CommandResult;
import com.neucore.voicesdk.model.VoiceIntent;

/**
 * 回复话术生成：执行结果 -> 播报文本。
 * <p>
 * 默认实现：{@link com.neucore.voicesdk.impl.TemplateReplyComposer}（模板 + 域词典）。
 * 自定义：可接入大模型生成或按业务定制措辞。
 */
public interface ReplyComposer {

    void init(Context context, VoiceSdkConfig config) throws Exception;

    /**
     * @param language zh / en
     * @return 可直接交给 TTS 的文本；返回 null 时链路会使用兜底话术
     */
    String compose(VoiceIntent intent, CommandResult result, String language);

    void release();
}
