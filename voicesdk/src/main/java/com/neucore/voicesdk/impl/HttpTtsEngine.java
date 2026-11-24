package com.neucore.voicesdk.impl;

import android.content.Context;
import android.media.MediaPlayer;

import com.neucore.voicesdk.VoiceSdkConfig;
import com.neucore.voicesdk.api.TtsCallback;
import com.neucore.voicesdk.api.TtsEngine;
import com.neucore.voicesdk.internal.HttpUtil;
import com.neucore.voicesdk.internal.VoiceLog;
import com.neucore.voicesdk.model.VoiceIntent;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * HTTP TTS 实现：请求语音服务返回 mp3/wav 音频流并播放。
 * <p>
 * 协议：
 * <pre>
 * POST {endpoint}/tts
 * {"text":"好的，已打开客厅灯","lang":"zh","action":"turn_on","area":"living_room","device":"light"}
 * 200 audio/mpeg（或 audio/wav）
 * </pre>
 * 适合"预合成音库"方案：服务端按 intent 直接返回对应 mp3，音质与延迟都优于实时合成。
 */
public class HttpTtsEngine implements TtsEngine {

    private static final int CONNECT_TIMEOUT_MS = 5000;
    private static final int READ_TIMEOUT_MS = 20000;

    private Context context;
    private String ttsUrl;
    private MediaPlayer player;

    @Override
    public void init(Context context, VoiceSdkConfig config) {
        this.context = context.getApplicationContext();
        this.ttsUrl = buildTtsUrl(config.getTtsEndpoint());
        VoiceLog.d("HttpTtsEngine endpoint=" + ttsUrl);
    }

    static String buildTtsUrl(String endpoint) {
        if (endpoint == null || endpoint.trim().isEmpty()) {
            endpoint = "http://127.0.0.1:8700";
        }
        String base = endpoint.trim();
        while (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        if (!base.endsWith("/tts")) {
            base = base + "/tts";
        }
        return base;
    }

    @Override
    public void speak(String text, String language, VoiceIntent intent, TtsCallback callback) {
        if (text == null || text.trim().isEmpty()) {
            if (callback != null) {
                callback.onDone();
            }
            return;
        }
        try {
            JSONObject req = new JSONObject();
            req.put("text", text);
            req.put("lang", language == null ? "zh" : language);
            if (intent != null) {
                req.put("action", intent.getAction() == null ? "" : intent.getAction());
                req.put("area", intent.getArea() == null ? "" : intent.getArea());
                req.put("device", intent.getDevice() == null ? "" : intent.getDevice());
                if (intent.getValue() != null) {
                    req.put("value", intent.getValue());
                }
                if (intent.getUnit() != null) {
                    req.put("unit", intent.getUnit());
                }
            }

            byte[] audio = HttpUtil.post(ttsUrl, req.toString().getBytes("UTF-8"),
                    "application/json", CONNECT_TIMEOUT_MS, READ_TIMEOUT_MS);
            if (audio == null || audio.length == 0) {
                throw new Exception("TTS 返回空音频");
            }

            File f = new File(context.getCacheDir(), "vsdk_tts_" + System.currentTimeMillis() + ".mp3");
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(audio);
            fos.flush();
            fos.close();

            playFile(f, callback);
        } catch (Exception e) {
            VoiceLog.w("HTTP TTS 失败：" + e.getMessage());
            if (callback != null) {
                callback.onError("HTTP TTS 失败：" + e.getMessage());
            }
        }
    }

    private void playFile(File file, final TtsCallback callback) throws Exception {
        stop();
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] ok = new boolean[]{false};

        MediaPlayer mp = new MediaPlayer();
        player = mp;
        mp.setDataSource(file.getAbsolutePath());
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                ok[0] = true;
                latch.countDown();
            }
        });
        mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                latch.countDown();
                return true;
            }
        });
        mp.prepare();
        mp.start();

        long timeout = Math.max(5000, Math.min(60000, file.length() / 8));
        latch.await(timeout, TimeUnit.MILLISECONDS);

        stop();
        //noinspection ResultOfMethodCallIgnored
        file.delete();

        if (callback != null) {
            if (ok[0]) {
                callback.onDone();
            } else {
                callback.onError("音频播放失败");
            }
        }
    }

    @Override
    public void stop() {
        if (player != null) {
            try {
                if (player.isPlaying()) {
                    player.stop();
                }
            } catch (Exception ignored) {
            }
            try {
                player.release();
            } catch (Exception ignored) {
            }
            player = null;
        }
    }

    @Override
    public void release() {
        stop();
    }
}
