package com.neucore.voicesdk.model;

import org.json.JSONObject;

/**
 * 结构化意图（字段与 Home Assistant intent/slot 规范对齐）。
 * <pre>
 * {
 *   "raw": "打开客厅的灯",
 *   "language": "zh",
 *   "intent": "HassTurnOn",
 *   "action": "turn_on",
 *   "device": "light",
 *   "area": "living_room",
 *   "value": null,
 *   "unit": null,
 *   "confidence": 0.95
 * }
 * </pre>
 */
public final class VoiceIntent {

    public static final String ACTION_TURN_ON = "turn_on";
    public static final String ACTION_TURN_OFF = "turn_off";
    public static final String ACTION_SET_VALUE = "set_value";
    public static final String ACTION_INCREASE = "increase";
    public static final String ACTION_DECREASE = "decrease";
    public static final String ACTION_LOCK = "lock";
    public static final String ACTION_UNLOCK = "unlock";
    public static final String ACTION_QUERY = "query";

    private String raw = "";
    private String language = "zh";
    private String intent = "Unknown";
    private String action;
    private String device;
    private String area = "default";
    private Integer value;
    private String unit;
    private float confidence = 0.5f;

    public VoiceIntent() {
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw == null ? "" : raw;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language == null ? "zh" : language;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent == null ? "Unknown" : intent;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area == null ? "default" : area;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public float getConfidence() {
        return confidence;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }

    public boolean isUnderstood() {
        return action != null && device != null;
    }

    public JSONObject toJson() {
        JSONObject o = new JSONObject();
        try {
            o.put("raw", raw);
            o.put("language", language);
            o.put("intent", intent);
            o.put("action", action == null ? JSONObject.NULL : action);
            o.put("device", device == null ? JSONObject.NULL : device);
            o.put("area", area);
            o.put("value", value == null ? JSONObject.NULL : value);
            o.put("unit", unit == null ? JSONObject.NULL : unit);
            o.put("confidence", confidence);
        } catch (Exception ignored) {
        }
        return o;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }
}
