package com.neucore.voicesdk.impl;

import android.content.Context;
import android.text.TextUtils;

import com.neucore.voicesdk.VoiceSdkConfig;
import com.neucore.voicesdk.api.ReplyComposer;
import com.neucore.voicesdk.model.CommandResult;
import com.neucore.voicesdk.model.VoiceIntent;

/**
 * 默认回复话术：模板 + 域词典（中英双语）。
 * <p>
 * 优先级：设备处理器自带 speechText > 模板 > 兜底话术。
 */
public class TemplateReplyComposer implements ReplyComposer {

    private DomainConfig domain;

    @Override
    public void init(Context context, VoiceSdkConfig config) {
        domain = DomainConfig.create(config.getDomainJson());
    }

    @Override
    public String compose(VoiceIntent intent, CommandResult result, String language) {
        if (domain == null) {
            domain = DomainConfig.createDefault();
        }
        String lang = TextUtils.isEmpty(language) ? "zh" : language;

        if (result != null && !TextUtils.isEmpty(result.getSpeechText())) {
            return result.getSpeechText();
        }
        if (result != null && !result.isSuccess()) {
            return domain.getFixed("fallback", lang);
        }
        if (intent == null || intent.getAction() == null) {
            return domain.getFixed("fallback", lang);
        }

        String action = intent.getAction();
        String area = domain.getAreaPhrase(intent.getArea(), lang);
        String device = domain.getDevicePhrase(intent.getDevice(), lang);

        if (VoiceIntent.ACTION_LOCK.equals(action) || VoiceIntent.ACTION_UNLOCK.equals(action)) {
            return domain.getFixed(action, lang);
        }

        if (VoiceIntent.ACTION_SET_VALUE.equals(action)) {
            String prefix = fill(domain.getSetValuePrefix(lang), area, device, lang);
            String value = intent.getValue() == null ? "" : String.valueOf(intent.getValue());
            String unit = unitSuffix(intent.getUnit(), lang);
            return "en".equals(lang) ? prefix + " " + value + unit + "." : prefix + value + unit;
        }

        String tpl = domain.getActionTemplate(action, lang);
        if (TextUtils.isEmpty(tpl)) {
            return domain.getFixed("fallback", lang);
        }
        String text = fill(tpl, area, device, lang);
        if (intent.getValue() != null) {
            text = text.replace("{value}", String.valueOf(intent.getValue()));
        }
        text = text.replace("{unit}", unitSuffix(intent.getUnit(), lang));
        return text;
    }

    @Override
    public void release() {
    }

    /**
     * 替换 {area}{device}：中文直接拼接，英文补空格。
     */
    private static String fill(String tpl, String area, String device, String lang) {
        String joined = "en".equals(lang) ? (area + " " + device).trim() : (area + device);
        String out = tpl.replace("{area}{device}", joined);
        out = out.replace("{area}", area);
        out = out.replace("{device}", device);
        return out;
    }

    private static String unitSuffix(String unit, String lang) {
        if ("degree".equals(unit)) {
            return "en".equals(lang) ? " degrees" : "度";
        }
        if ("percent".equals(unit)) {
            return "%";
        }
        return "";
    }
}
