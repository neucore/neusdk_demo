package com.neucore.voicesdk.model;

/**
 * 语音识别结果。
 */
public final class AsrResult {

    private final String text;
    private final String language;
    private final float confidence;

    public AsrResult(String text, String language, float confidence) {
        this.text = text == null ? "" : text;
        this.language = language == null ? "" : language;
        this.confidence = confidence;
    }

    public String getText() {
        return text;
    }

    public String getLanguage() {
        return language;
    }

    public float getConfidence() {
        return confidence;
    }

    public boolean isEmpty() {
        return text.trim().length() == 0;
    }

    @Override
    public String toString() {
        return "AsrResult{text='" + text + "', language='" + language + "', confidence=" + confidence + "}";
    }
}
