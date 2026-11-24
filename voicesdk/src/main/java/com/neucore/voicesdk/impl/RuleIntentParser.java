package com.neucore.voicesdk.impl;

import android.content.Context;
import android.text.TextUtils;

import com.neucore.voicesdk.VoiceSdkConfig;
import com.neucore.voicesdk.api.IntentParser;
import com.neucore.voicesdk.model.VoiceIntent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 规则意图解析（中英双语，离线，毫秒级）。
 * <p>
 * 与端侧 Python 版 03_intent.py 的词典、动作优先级、数值抽取规则保持一致。
 * 想接入模型/云 NLU 时，实现 {@link IntentParser} 并在配置里替换类名即可。
 */
public class RuleIntentParser implements IntentParser {

    /** 顺序即优先级：先匹配到的胜出 */
    private static final String[][] ACTION_RULES = new String[][]{
            {"调到|调至|设为|设置成|设定为|set .*? to|set to|adjust .*? to|change .*? to|dim .*? to|brighten .*? to", VoiceIntent.ACTION_SET_VALUE},
            {"锁上|上锁|锁门|把门锁|lock the|lock ", VoiceIntent.ACTION_LOCK},
            {"解锁|开锁|unlock", VoiceIntent.ACTION_UNLOCK},
            {"调高|升高|加大|增大|提高|开大|大一点|高一点|turn up|increase|raise|higher|louder|brighter", VoiceIntent.ACTION_INCREASE},
            {"调低|降低|减小|调小|关小|小一点|低一点|turn down|decrease|lower|quieter|dimmer", VoiceIntent.ACTION_DECREASE},
            {"关闭|关掉|关上|关一下|^关|\\bturn off\\b|\\bswitch off\\b|\\bpower off\\b|\\bshut off\\b|\\bclose\\b", VoiceIntent.ACTION_TURN_OFF},
            {"打开|开启|启动|开一下|^开|turn on|switch on|power on|open", VoiceIntent.ACTION_TURN_ON},
            {"多少|几度|状态|开着吗|关着吗|是不是开|what|is it|status|is .*? on", VoiceIntent.ACTION_QUERY}
    };

    private static final Pattern NUMBER_PATTERN = Pattern.compile("(\\d+)\\s*(%|度|℃|percent|degrees?)?");
    private static final Pattern CN_DEGREE_PATTERN = Pattern.compile("([零一二两三四五六七八九十]{1,3})\\s*(度|℃)");
    private static final Pattern CN_NUMBER_PATTERN = Pattern.compile("([零一二两三四五六七八九十]{1,3})");

    private static final Map<String, Integer> CN_DIGIT = new LinkedHashMap<String, Integer>();
    private static final Map<String, Integer> EN_ONES = new LinkedHashMap<String, Integer>();
    private static final Map<String, Integer> EN_TENS = new LinkedHashMap<String, Integer>();

    static {
        CN_DIGIT.put("零", 0);
        CN_DIGIT.put("一", 1);
        CN_DIGIT.put("二", 2);
        CN_DIGIT.put("两", 2);
        CN_DIGIT.put("三", 3);
        CN_DIGIT.put("四", 4);
        CN_DIGIT.put("五", 5);
        CN_DIGIT.put("六", 6);
        CN_DIGIT.put("七", 7);
        CN_DIGIT.put("八", 8);
        CN_DIGIT.put("九", 9);

        EN_ONES.put("zero", 0);
        EN_ONES.put("one", 1);
        EN_ONES.put("two", 2);
        EN_ONES.put("three", 3);
        EN_ONES.put("four", 4);
        EN_ONES.put("five", 5);
        EN_ONES.put("six", 6);
        EN_ONES.put("seven", 7);
        EN_ONES.put("eight", 8);
        EN_ONES.put("nine", 9);
        EN_ONES.put("ten", 10);
        EN_ONES.put("eleven", 11);
        EN_ONES.put("twelve", 12);
        EN_ONES.put("thirteen", 13);
        EN_ONES.put("fourteen", 14);
        EN_ONES.put("fifteen", 15);
        EN_ONES.put("sixteen", 16);
        EN_ONES.put("seventeen", 17);
        EN_ONES.put("eighteen", 18);
        EN_ONES.put("nineteen", 19);

        EN_TENS.put("twenty", 20);
        EN_TENS.put("thirty", 30);
        EN_TENS.put("forty", 40);
        EN_TENS.put("fifty", 50);
        EN_TENS.put("sixty", 60);
        EN_TENS.put("seventy", 70);
        EN_TENS.put("eighty", 80);
        EN_TENS.put("ninety", 90);
    }

    private static final Map<String, String> EXTRA_INTENT = new LinkedHashMap<String, String>();

    static {
        EXTRA_INTENT.put(VoiceIntent.ACTION_SET_VALUE, "HassSetValue");
        EXTRA_INTENT.put(VoiceIntent.ACTION_LOCK, "HassLock");
        EXTRA_INTENT.put(VoiceIntent.ACTION_UNLOCK, "HassUnlock");
    }

    private DomainConfig domain;

    @Override
    public void init(Context context, VoiceSdkConfig config) {
        domain = DomainConfig.create(config.getDomainJson());
    }

    @Override
    public VoiceIntent parse(String text, String language) {
        if (domain == null) {
            domain = DomainConfig.createDefault();
        }
        String raw = text == null ? "" : text;
        String low = raw.toLowerCase().trim();
        String lang = TextUtils.isEmpty(language)
                ? (containsChinese(raw) ? "zh" : "en") : language;

        String action = null;
        for (String[] rule : ACTION_RULES) {
            if (Pattern.compile(rule[0]).matcher(low).find()) {
                action = rule[1];
                break;
            }
        }

        String device = findKey(domain.getDeviceSynonyms(), low);
        String area = findKey(domain.getAreaSynonyms(), low);

        Integer value = null;
        String unit = null;
        Object[] v = extractValue(low);
        if (v != null) {
            value = (Integer) v[0];
            unit = (String) v[1];
        }
        if (unit != null) {
            if (!"%".equals(unit) && !"percent".equals(unit)) {
                unit = "degree";
            } else {
                unit = "percent";
            }
        }

        float conf = 0.5f + (action != null ? 0.3f : 0f) + (device != null ? 0.2f : 0f);
        if (action != null && device == null) {
            conf -= 0.15f;
        }
        conf = Math.min(conf, 0.95f);
        conf = Math.round(conf * 100) / 100f;

        VoiceIntent intent = new VoiceIntent();
        intent.setRaw(raw);
        intent.setLanguage(lang);
        intent.setAction(action);
        intent.setDevice(device);
        intent.setArea(area == null ? "default" : area);
        intent.setValue(value);
        intent.setUnit(unit);
        intent.setConfidence(conf);
        String intentName = action == null ? null : EXTRA_INTENT.get(action);
        if (intentName == null) {
            intentName = action == null ? "Unknown" : domain.getActionIntent(action);
        }
        intent.setIntent(intentName);
        return intent;
    }

    @Override
    public void release() {
    }

    private static String findKey(List<String[]> synonyms, String text) {
        for (String[] pair : synonyms) {
            if (hit(pair[0], text)) {
                return pair[1];
            }
        }
        return null;
    }

    /**
     * 中文用子串匹配；英文用词边界，避免 small 命中 all、lights 命中 light。
     */
    static boolean hit(String key, String text) {
        if (isAscii(key)) {
            return Pattern.compile("\\b" + Pattern.quote(key) + "\\b").matcher(text).find();
        }
        return text.contains(key);
    }

    private static boolean isAscii(String s) {
        if (s == null || s.isEmpty()) {
            return true;
        }
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) >= 128) {
                return false;
            }
        }
        return true;
    }

    private static boolean containsChinese(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= '\u4e00' && c <= '\u9fa5') {
                return true;
            }
        }
        return false;
    }

    /**
     * 抽数值：26 / 二十六 / 50% / 26度 / twenty six。
     *
     * @return {Integer value, String unit} 或 null
     */
    static Object[] extractValue(String text) {
        Matcher m = NUMBER_PATTERN.matcher(text);
        if (m.find()) {
            Integer v = parseIntSafe(m.group(1));
            if (v != null) {
                return new Object[]{v, m.group(2)};
            }
        }
        m = CN_DEGREE_PATTERN.matcher(text);
        if (m.find()) {
            Integer v = cn2num(m.group(1));
            if (v != null) {
                return new Object[]{v, m.group(2)};
            }
        }
        if (text.contains("半")) {
            // "打开一半窗帘" 不是数值指令
            return null;
        }
        m = CN_NUMBER_PATTERN.matcher(text);
        if (m.find()) {
            Integer v = cn2num(m.group(1));
            if (v != null) {
                return new Object[]{v, null};
            }
        }
        Integer v = extractEnNum(text);
        if (v != null) {
            String unit = null;
            if (text.contains("percent")) {
                unit = "percent";
            } else if (text.contains("degree")) {
                unit = "degree";
            }
            return new Object[]{v, unit};
        }
        return null;
    }

    private static Integer parseIntSafe(String s) {
        try {
            return Integer.valueOf(s);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 把 0-99 的中文数字转成 int，失败返回 null。
     */
    static Integer cn2num(String s) {
        if (TextUtils.isEmpty(s)) {
            return null;
        }
        if (isAllDigits(s)) {
            return parseIntSafe(s);
        }
        int idx = s.indexOf('十');
        if (idx >= 0) {
            String left = s.substring(0, idx);
            String right = s.substring(idx + 1);
            int tens = left.isEmpty() ? 1 : (CN_DIGIT.containsKey(left) ? CN_DIGIT.get(left) : 1);
            int ones = right.isEmpty() ? 0 : (CN_DIGIT.containsKey(right) ? CN_DIGIT.get(right) : 0);
            return tens * 10 + ones;
        }
        if (s.length() == 1 && CN_DIGIT.containsKey(s)) {
            return CN_DIGIT.get(s);
        }
        return null;
    }

    private static boolean isAllDigits(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return !s.isEmpty();
    }

    /**
     * 把 'twenty six' 这类英文数字词转成 int。
     */
    static Integer en2num(String words) {
        int total = 0;
        String[] parts = words.toLowerCase().replace("-", " ").split("\\s+");
        for (String w : parts) {
            if (EN_ONES.containsKey(w)) {
                total += EN_ONES.get(w);
            } else if (EN_TENS.containsKey(w)) {
                total += EN_TENS.get(w);
            } else if ("hundred".equals(w)) {
                total = Math.max(total, 1) * 100;
            } else if ("and".equals(w) || "point".equals(w)) {
                // 忽略
            } else {
                return null;
            }
        }
        return total == 0 ? null : total;
    }

    /**
     * 在英文句子里找最长的连续数字词序列。
     */
    static Integer extractEnNum(String text) {
        String[] toks = text.toLowerCase().split("[\\s\\-]+");
        List<String> best = new ArrayList<String>();
        List<String> cur = new ArrayList<String>();
        for (String t : toks) {
            if (EN_ONES.containsKey(t) || EN_TENS.containsKey(t) || "hundred".equals(t)) {
                cur.add(t);
                if (cur.size() > best.size()) {
                    best = new ArrayList<String>(cur);
                }
            } else {
                cur.clear();
            }
        }
        if (best.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String w : best) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(w);
        }
        return en2num(sb.toString());
    }
}
