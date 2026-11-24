package com.neucore.voicesdk.impl;

import android.text.TextUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 领域配置：区域 / 设备 / 动作 / 话术模板。
 * <p>
 * 默认使用内置域（与端侧 domain.json 保持一致），宿主可通过
 * {@link com.neucore.voicesdk.VoiceSdkConfig.Builder#domainJson(String)} 传入自己的
 * domain.json 内容来扩展区域、设备、话术，无需改动 SDK 代码。
 */
public final class DomainConfig {

    /**
     * 内置域定义。字段含义：
     * <ul>
     *   <li>areas/devices：key 为内部标识，zh/en 为同义词，phrase_* 为播报用词组</li>
     *   <li>actions：intent 为 HA 意图名，zh_tpl/en_tpl 为回复模板</li>
     *   <li>set_value：数值类指令前缀</li>
     *   <li>fixed：固定话术（锁/解锁/兜底/确认）</li>
     * </ul>
     */
    public static final String DEFAULT_DOMAIN_JSON = "{"
            + "\"areas\":{"
            + "\"living_room\":{\"zh\":[\"客厅\",\"起居室\"],\"en\":[\"living room\",\"lounge\"],\"phrase_zh\":\"客厅\",\"phrase_en\":\"living room\"},"
            + "\"bedroom\":{\"zh\":[\"卧室\",\"主卧\",\"次卧\"],\"en\":[\"bedroom\",\"master bedroom\"],\"phrase_zh\":\"卧室\",\"phrase_en\":\"bedroom\"},"
            + "\"kitchen\":{\"zh\":[\"厨房\"],\"en\":[\"kitchen\"],\"phrase_zh\":\"厨房\",\"phrase_en\":\"kitchen\"},"
            + "\"bathroom\":{\"zh\":[\"卫生间\",\"浴室\",\"厕所\"],\"en\":[\"bathroom\",\"toilet\"],\"phrase_zh\":\"卫生间\",\"phrase_en\":\"bathroom\"},"
            + "\"study\":{\"zh\":[\"书房\"],\"en\":[\"study\",\"office\"],\"phrase_zh\":\"书房\",\"phrase_en\":\"study\"},"
            + "\"balcony\":{\"zh\":[\"阳台\"],\"en\":[\"balcony\"],\"phrase_zh\":\"阳台\",\"phrase_en\":\"balcony\"},"
            + "\"dining_room\":{\"zh\":[\"餐厅\"],\"en\":[\"dining room\"],\"phrase_zh\":\"餐厅\",\"phrase_en\":\"dining room\"},"
            + "\"entrance\":{\"zh\":[\"玄关\"],\"en\":[\"entrance\",\"hallway\"],\"phrase_zh\":\"玄关\",\"phrase_en\":\"entrance\"},"
            + "\"garage\":{\"zh\":[\"车库\"],\"en\":[\"garage\"],\"phrase_zh\":\"车库\",\"phrase_en\":\"garage\"},"
            + "\"walk_in_closet\":{\"zh\":[\"衣帽间\",\"更衣室\"],\"en\":[\"walk-in closet\",\"closet\"],\"phrase_zh\":\"衣帽间\",\"phrase_en\":\"walk-in closet\"},"
            + "\"all\":{\"zh\":[\"全屋\",\"所有\",\"全部\"],\"en\":[\"everywhere\",\"all rooms\",\"all\"],\"phrase_zh\":\"所有\",\"phrase_en\":\"all\"},"
            + "\"default\":{\"zh\":[],\"en\":[],\"phrase_zh\":\"\",\"phrase_en\":\"\"}"
            + "},"
            + "\"devices\":{"
            + "\"light\":{\"zh\":[\"灯光\",\"灯\",\"照明\"],\"en\":[\"light\",\"lights\",\"lamp\"],\"phrase_zh\":\"灯\",\"phrase_en\":\"light\"},"
            + "\"ac\":{\"zh\":[\"空调\",\"冷气\"],\"en\":[\"air conditioner\",\"ac\",\"aircon\"],\"phrase_zh\":\"空调\",\"phrase_en\":\"AC\"},"
            + "\"heater\":{\"zh\":[\"暖气\",\"地暖\"],\"en\":[\"heater\",\"heating\"],\"phrase_zh\":\"暖气\",\"phrase_en\":\"heater\"},"
            + "\"curtain\":{\"zh\":[\"窗帘\",\"帘子\"],\"en\":[\"curtain\",\"curtains\",\"blinds\"],\"phrase_zh\":\"窗帘\",\"phrase_en\":\"curtain\"},"
            + "\"fan\":{\"zh\":[\"风扇\",\"电扇\"],\"en\":[\"fan\"],\"phrase_zh\":\"风扇\",\"phrase_en\":\"fan\"},"
            + "\"tv\":{\"zh\":[\"电视\",\"电视机\"],\"en\":[\"tv\",\"television\"],\"phrase_zh\":\"电视\",\"phrase_en\":\"TV\"},"
            + "\"humidifier\":{\"zh\":[\"加湿器\"],\"en\":[\"humidifier\"],\"phrase_zh\":\"加湿器\",\"phrase_en\":\"humidifier\"},"
            + "\"vacuum\":{\"zh\":[\"扫地机器人\",\"吸尘器\"],\"en\":[\"vacuum\",\"robot vacuum\"],\"phrase_zh\":\"扫地机器人\",\"phrase_en\":\"robot vacuum\"},"
            + "\"purifier\":{\"zh\":[\"空气净化器\",\"净化器\"],\"en\":[\"air purifier\",\"purifier\"],\"phrase_zh\":\"空气净化器\",\"phrase_en\":\"air purifier\"},"
            + "\"lock\":{\"zh\":[\"门锁\",\"大门\",\"门\"],\"en\":[\"door\",\"lock\"],\"phrase_zh\":\"门锁\",\"phrase_en\":\"door lock\"}"
            + "},"
            + "\"actions\":{"
            + "\"turn_on\":{\"intent\":\"HassTurnOn\",\"zh_tpl\":\"好的，已打开{area}{device}\",\"en_tpl\":\"OK, {area}{device} is on.\"},"
            + "\"turn_off\":{\"intent\":\"HassTurnOff\",\"zh_tpl\":\"好的，已关闭{area}{device}\",\"en_tpl\":\"OK, {area}{device} is off.\"},"
            + "\"increase\":{\"intent\":\"HassIncrease\",\"zh_tpl\":\"好的，已调高{area}{device}\",\"en_tpl\":\"OK, turned up {area}{device}.\"},"
            + "\"decrease\":{\"intent\":\"HassDecrease\",\"zh_tpl\":\"好的，已调低{area}{device}\",\"en_tpl\":\"OK, turned down {area}{device}.\"},"
            + "\"query\":{\"intent\":\"HassQuery\",\"zh_tpl\":\"正在查询{area}{device}的状态\",\"en_tpl\":\"Checking {area}{device} status.\"}"
            + "},"
            + "\"set_value\":{\"devices\":[\"ac\",\"heater\",\"light\",\"fan\",\"curtain\"],"
            + "\"zh_prefix\":\"好的，已把{area}{device}调到\",\"en_prefix\":\"OK, setting {area}{device} to\"},"
            + "\"fixed\":{"
            + "\"lock\":{\"zh\":\"好的，门已上锁\",\"en\":\"OK, the door is locked.\"},"
            + "\"unlock\":{\"zh\":\"好的，门已解锁\",\"en\":\"OK, the door is unlocked.\"},"
            + "\"fallback\":{\"zh\":\"抱歉，我没听懂\",\"en\":\"Sorry, I didn't catch that.\"},"
            + "\"ack\":{\"zh\":\"好的\",\"en\":\"OK.\"}"
            + "}"
            + "}";

    private final Map<String, Area> areas = new LinkedHashMap<String, Area>();
    private final Map<String, Device> devices = new LinkedHashMap<String, Device>();
    private final Map<String, Action> actions = new LinkedHashMap<String, Action>();
    private final Map<String, String> fixedKeyZh = new LinkedHashMap<String, String>();
    private final Map<String, String> fixedKeyEn = new LinkedHashMap<String, String>();
    private final List<String[]> areaSynonyms = new ArrayList<String[]>();
    private final List<String[]> deviceSynonyms = new ArrayList<String[]>();
    private String setValuePrefixZh = "好的，已把{area}{device}调到";
    private String setValuePrefixEn = "OK, setting {area}{device} to";

    private DomainConfig() {
    }

    public static DomainConfig create(String json) {
        DomainConfig d = new DomainConfig();
        d.parse(TextUtils.isEmpty(json) ? DEFAULT_DOMAIN_JSON : json);
        return d;
    }

    public static DomainConfig createDefault() {
        return create(DEFAULT_DOMAIN_JSON);
    }

    private void parse(String json) {
        try {
            JSONObject root = new JSONObject(json);
            JSONObject areasObj = root.optJSONObject("areas");
            if (areasObj != null) {
                for (java.util.Iterator<String> it = areasObj.keys(); it.hasNext(); ) {
                    String key = it.next();
                    JSONObject o = areasObj.optJSONObject(key);
                    if (o == null) {
                        continue;
                    }
                    Area a = new Area(key);
                    a.zh = toStringList(o.optJSONArray("zh"));
                    a.en = toStringList(o.optJSONArray("en"));
                    a.phraseZh = o.optString("phrase_zh", "");
                    a.phraseEn = o.optString("phrase_en", "");
                    areas.put(key, a);
                    for (String s : a.zh) {
                        areaSynonyms.add(new String[]{s, key});
                    }
                    for (String s : a.en) {
                        areaSynonyms.add(new String[]{s, key});
                    }
                }
            }
            JSONObject devicesObj = root.optJSONObject("devices");
            if (devicesObj != null) {
                for (java.util.Iterator<String> it = devicesObj.keys(); it.hasNext(); ) {
                    String key = it.next();
                    JSONObject o = devicesObj.optJSONObject(key);
                    if (o == null) {
                        continue;
                    }
                    Device dev = new Device(key);
                    dev.zh = toStringList(o.optJSONArray("zh"));
                    dev.en = toStringList(o.optJSONArray("en"));
                    dev.phraseZh = o.optString("phrase_zh", "");
                    dev.phraseEn = o.optString("phrase_en", "");
                    devices.put(key, dev);
                    for (String s : dev.zh) {
                        deviceSynonyms.add(new String[]{s, key});
                    }
                    for (String s : dev.en) {
                        deviceSynonyms.add(new String[]{s, key});
                    }
                }
            }
            JSONObject actionsObj = root.optJSONObject("actions");
            if (actionsObj != null) {
                for (java.util.Iterator<String> it = actionsObj.keys(); it.hasNext(); ) {
                    String key = it.next();
                    JSONObject o = actionsObj.optJSONObject(key);
                    if (o == null) {
                        continue;
                    }
                    Action a = new Action();
                    a.intent = o.optString("intent", "Unknown");
                    a.zhTpl = o.optString("zh_tpl", "");
                    a.enTpl = o.optString("en_tpl", "");
                    actions.put(key, a);
                }
            }
            JSONObject setValue = root.optJSONObject("set_value");
            if (setValue != null) {
                setValuePrefixZh = setValue.optString("zh_prefix", setValuePrefixZh);
                setValuePrefixEn = setValue.optString("en_prefix", setValuePrefixEn);
            }
            JSONObject fixed = root.optJSONObject("fixed");
            if (fixed != null) {
                for (java.util.Iterator<String> it = fixed.keys(); it.hasNext(); ) {
                    String key = it.next();
                    JSONObject o = fixed.optJSONObject(key);
                    if (o == null) {
                        continue;
                    }
                    fixedKeyZh.put(key, o.optString("zh", ""));
                    fixedKeyEn.put(key, o.optString("en", ""));
                }
            }
        } catch (Exception e) {
            // 解析失败时退回内置域（内置域仍失败则保持空域，避免死循环）
            areas.clear();
            devices.clear();
            actions.clear();
            areaSynonyms.clear();
            deviceSynonyms.clear();
            fixedKeyZh.clear();
            fixedKeyEn.clear();
            if (!DEFAULT_DOMAIN_JSON.equals(json)) {
                parse(DEFAULT_DOMAIN_JSON);
            }
        }
    }

    private static List<String> toStringList(org.json.JSONArray arr) {
        List<String> list = new ArrayList<String>();
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                String s = arr.optString(i, "");
                if (!TextUtils.isEmpty(s)) {
                    list.add(s);
                }
            }
        }
        return list;
    }

    /**
     * 区域同义词列表，元素为 {同义词, 区域key}，保持配置顺序（先匹配先赢）。
     */
    public List<String[]> getAreaSynonyms() {
        return areaSynonyms;
    }

    public List<String[]> getDeviceSynonyms() {
        return deviceSynonyms;
    }

    public String getAreaPhrase(String areaKey, String language) {
        Area a = areas.get(areaKey);
        if (a == null) {
            return "";
        }
        return "en".equals(language) ? a.phraseEn : a.phraseZh;
    }

    public String getDevicePhrase(String deviceKey, String language) {
        Device d = devices.get(deviceKey);
        if (d == null) {
            return "en".equals(language) ? "device" : "设备";
        }
        return "en".equals(language) ? d.phraseEn : d.phraseZh;
    }

    public String getActionIntent(String action) {
        Action a = actions.get(action);
        return a == null ? "Unknown" : a.intent;
    }

    public String getActionTemplate(String action, String language) {
        Action a = actions.get(action);
        if (a == null) {
            return null;
        }
        return "en".equals(language) ? a.enTpl : a.zhTpl;
    }

    public String getSetValuePrefix(String language) {
        return "en".equals(language) ? setValuePrefixEn : setValuePrefixZh;
    }

    public String getFixed(String key, String language) {
        String s = "en".equals(language) ? fixedKeyEn.get(key) : fixedKeyZh.get(key);
        if (TextUtils.isEmpty(s)) {
            s = "en".equals(language) ? "Sorry, I didn't catch that." : "抱歉，我没听懂";
        }
        return s;
    }

    private static final class Area {
        final String key;
        List<String> zh = new ArrayList<String>();
        List<String> en = new ArrayList<String>();
        String phraseZh = "";
        String phraseEn = "";

        Area(String key) {
            this.key = key;
        }
    }

    private static final class Device {
        final String key;
        List<String> zh = new ArrayList<String>();
        List<String> en = new ArrayList<String>();
        String phraseZh = "";
        String phraseEn = "";

        Device(String key) {
            this.key = key;
        }
    }

    private static final class Action {
        String intent = "Unknown";
        String zhTpl = "";
        String enTpl = "";
    }
}
