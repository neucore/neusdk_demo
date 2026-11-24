package com.neucore.voicesdk.impl;

import android.content.Context;

import com.neucore.voicesdk.VoiceSdkConfig;
import com.neucore.voicesdk.api.AsrEngine;
import com.neucore.voicesdk.internal.HttpUtil;
import com.neucore.voicesdk.internal.VoiceLog;
import com.neucore.voicesdk.model.AsrResult;

import org.json.JSONObject;

/**
 * 默认 ASR 实现：把 PCM 直接 POST 给语音服务。
 * <p>
 * 协议（与端侧 voice-server 对齐）：
 * <pre>
 * POST {endpoint}/asr?sr=16000
 * Content-Type: application/octet-stream
 * Body: 16bit little-endian 单声道 PCM
 *
 * 200 {"text":"打开客厅的灯","lang":"zh"}
 * </pre>
 * endpoint 已包含 /asr 时不再追加。服务不可用时抛异常，由链路回调 onError。
 * <p>
 * 想换成端侧离线模型（sherpa-onnx 等），实现 {@link AsrEngine} 后把
 * 配置 engines.asr 指向自己的类即可。
 */
public class HttpAsrEngine implements AsrEngine {

    private static final int CONNECT_TIMEOUT_MS = 5000;
    private static final int READ_TIMEOUT_MS = 20000;

    private String asrUrl;
    private VoiceSdkConfig config;

    @Override
    public void init(Context context, VoiceSdkConfig config) {
        this.config = config;
        this.asrUrl = buildAsrUrl(config.getAsrEndpoint());
        VoiceLog.d("HttpAsrEngine endpoint=" + asrUrl);
    }

    static String buildAsrUrl(String endpoint) {
        if (endpoint == null || endpoint.trim().isEmpty()) {
            endpoint = "http://127.0.0.1:8700";
        }
        String base = endpoint.trim();
        while (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        if (!base.endsWith("/asr")) {
            base = base + "/asr";
        }
        if (base.contains("?")) {
            return base + "&sr=16000&format=pcm16";
        }
        return base + "?sr=16000&format=pcm16";
    }

    @Override
    public AsrResult transcribe(short[] pcm, int sampleRate) throws Exception {
        if (pcm == null || pcm.length == 0) {
            return new AsrResult("", config == null ? "zh" : config.getLanguage(), 0f);
        }
        byte[] body = toLittleEndianBytes(pcm);
        String url = asrUrl;
        if (sampleRate > 0 && !url.contains("sr=")) {
            url = url + (url.contains("?") ? "&" : "?") + "sr=" + sampleRate;
        }
        VoiceLog.d("ASR 请求 " + url + "，PCM " + pcm.length + " samples");

        byte[] resp = HttpUtil.post(url, body, "application/octet-stream",
                CONNECT_TIMEOUT_MS, READ_TIMEOUT_MS);
        String json = new String(resp, "UTF-8");
        VoiceLog.d("ASR 响应 " + json);

        JSONObject o = new JSONObject(json);
        String text = o.optString("text", "");
        String lang = o.optString("lang", "");
        if (lang.isEmpty()) {
            lang = containsChinese(text) ? "zh" : "en";
        }
        float confidence = (float) o.optDouble("confidence", text.isEmpty() ? 0f : 0.9);
        return new AsrResult(text, lang, confidence);
    }

    @Override
    public void release() {
    }

    private static byte[] toLittleEndianBytes(short[] pcm) {
        byte[] out = new byte[pcm.length * 2];
        for (int i = 0; i < pcm.length; i++) {
            out[i * 2] = (byte) (pcm[i] & 0xff);
            out[i * 2 + 1] = (byte) ((pcm[i] >> 8) & 0xff);
        }
        return out;
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
}
