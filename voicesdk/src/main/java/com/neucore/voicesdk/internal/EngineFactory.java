package com.neucore.voicesdk.internal;

import android.content.Context;

import com.neucore.voicesdk.VoiceSdkConfig;
import com.neucore.voicesdk.api.AsrEngine;
import com.neucore.voicesdk.api.CommandExecutor;
import com.neucore.voicesdk.api.IntentParser;
import com.neucore.voicesdk.api.ReplyComposer;
import com.neucore.voicesdk.api.TtsEngine;
import com.neucore.voicesdk.impl.AndroidTtsEngine;
import com.neucore.voicesdk.impl.DefaultCommandExecutor;
import com.neucore.voicesdk.impl.HttpAsrEngine;
import com.neucore.voicesdk.impl.RuleIntentParser;
import com.neucore.voicesdk.impl.TemplateReplyComposer;

/**
 * 按配置中的类名反射创建引擎实例；失败时退回默认实现。
 * <p>
 * 这是"覆盖本地实现"的入口：宿主把自己的实现类名写进配置即可，
 * SDK 不需要重新编译。
 */
public final class EngineFactory {

    private EngineFactory() {
    }

    public static AsrEngine createAsr(Context context, VoiceSdkConfig config) throws Exception {
        return instantiate(AsrEngine.class, config.getAsrEngineClass(), HttpAsrEngine.class);
    }

    public static IntentParser createIntentParser(Context context, VoiceSdkConfig config) throws Exception {
        return instantiate(IntentParser.class, config.getIntentParserClass(), RuleIntentParser.class);
    }

    public static CommandExecutor createCommandExecutor(Context context, VoiceSdkConfig config) throws Exception {
        return instantiate(CommandExecutor.class, config.getCommandExecutorClass(), DefaultCommandExecutor.class);
    }

    public static ReplyComposer createReplyComposer(Context context, VoiceSdkConfig config) throws Exception {
        return instantiate(ReplyComposer.class, config.getReplyComposerClass(), TemplateReplyComposer.class);
    }

    public static TtsEngine createTts(Context context, VoiceSdkConfig config) throws Exception {
        return instantiate(TtsEngine.class, config.getTtsEngineClass(), AndroidTtsEngine.class);
    }

    @SuppressWarnings("unchecked")
    private static <T> T instantiate(Class<T> type, String className, Class<? extends T> fallback)
            throws Exception {
        if (className != null && className.trim().length() > 0) {
            try {
                Class<?> clazz = Class.forName(className.trim());
                Object instance = clazz.newInstance();
                if (type.isInstance(instance)) {
                    VoiceLog.d("引擎加载：" + className);
                    return (T) instance;
                }
                VoiceLog.w("类 " + className + " 未实现 " + type.getSimpleName() + "，使用默认实现");
            } catch (Throwable t) {
                VoiceLog.w("加载 " + className + " 失败（" + t.getClass().getSimpleName()
                        + "），使用默认实现 " + fallback.getSimpleName());
            }
        }
        VoiceLog.d("引擎加载（默认）：" + fallback.getName());
        return fallback.newInstance();
    }
}
