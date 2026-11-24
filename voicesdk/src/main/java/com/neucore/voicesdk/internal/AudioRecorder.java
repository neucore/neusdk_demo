package com.neucore.voicesdk.internal;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.neucore.voicesdk.VoiceSdkConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 麦克风录音 + 能量 VAD。
 * <p>
 * 规则与端侧 Python 版本保持一致：
 * <ul>
 *   <li>16kHz / 单声道 / 16bit</li>
 *   <li>RMS 超过阈值开始记录，连续静音超过 silenceMs 判定结束</li>
 *   <li>结束后补 1 秒静音，否则 ASR 末词容易丢</li>
 *   <li>单句最长 maxRecordSeconds 秒</li>
 * </ul>
 */
public final class AudioRecorder {

    public interface CancelChecker {
        boolean isCancelled();
    }

    public static final class Result {
        public final short[] pcm;
        public final int sampleRate;
        public final long speechMs;

        public Result(short[] pcm, int sampleRate, long speechMs) {
            this.pcm = pcm;
            this.sampleRate = sampleRate;
            this.speechMs = speechMs;
        }

        public boolean isEmpty() {
            return pcm == null || pcm.length == 0;
        }
    }

    private AudioRecorder() {
    }

    public static Result record(VoiceSdkConfig config, CancelChecker checker) throws IOException {
        int sampleRate = config.getSampleRate();
        int frameSamples = Math.max(160, sampleRate * config.getFrameMs() / 1000);
        int silenceFrames = Math.max(1, config.getSilenceMs() / Math.max(1, config.getFrameMs()));
        int maxFrames = Math.max(1, config.getMaxRecordSeconds() * 1000 / Math.max(1, config.getFrameMs()));

        int minBuffer = AudioRecord.getMinBufferSize(sampleRate,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        if (minBuffer <= 0) {
            throw new IOException("设备不支持 " + sampleRate + "Hz 单声道录音（getMinBufferSize=" + minBuffer + "）");
        }
        int bufferSize = Math.max(minBuffer, frameSamples * 2 * 4);

        AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        if (record.getState() != AudioRecord.STATE_INITIALIZED) {
            record.release();
            throw new IOException("AudioRecord 初始化失败，请检查 RECORD_AUDIO 权限与麦克风占用");
        }

        List<short[]> chunks = new ArrayList<short[]>();
        int totalSamples = 0;
        boolean speechStarted = false;
        int silentCount = 0;
        int frames = 0;

        try {
            record.startRecording();
            short[] buf = new short[frameSamples];
            while (true) {
                if (checker != null && checker.isCancelled()) {
                    VoiceLog.d("录音被取消");
                    break;
                }
                int n = record.read(buf, 0, frameSamples);
                if (n <= 0) {
                    if (n == AudioRecord.ERROR_INVALID_OPERATION || n == AudioRecord.ERROR_BAD_VALUE) {
                        throw new IOException("AudioRecord.read 错误：" + n);
                    }
                    continue;
                }
                frames++;
                double sum = 0;
                for (int i = 0; i < n; i++) {
                    double v = buf[i] / 32768.0;
                    sum += v * v;
                }
                double rms = Math.sqrt(sum / n);

                if (rms > config.getSilenceRms()) {
                    speechStarted = true;
                    silentCount = 0;
                } else if (speechStarted) {
                    silentCount++;
                }

                if (speechStarted) {
                    short[] copy = new short[n];
                    System.arraycopy(buf, 0, copy, 0, n);
                    chunks.add(copy);
                    totalSamples += n;
                }

                if (speechStarted && silentCount >= silenceFrames) {
                    break;
                }
                if (frames >= maxFrames) {
                    break;
                }
            }
        } finally {
            try {
                record.stop();
            } catch (Exception ignored) {
            }
            record.release();
        }

        if (totalSamples < sampleRate / 4) {
            VoiceLog.d("有效语音不足 0.25s，丢弃");
            return new Result(new short[0], sampleRate, 0);
        }

        // 尾部补 1 秒静音：sherpa-onnx 等流式 ASR 必须
        short[] tail = new short[sampleRate];
        short[] pcm = new short[totalSamples + tail.length];
        int offset = 0;
        for (short[] c : chunks) {
            System.arraycopy(c, 0, pcm, offset, c.length);
            offset += c.length;
        }
        System.arraycopy(tail, 0, pcm, offset, tail.length);

        long speechMs = (long) totalSamples * 1000 / sampleRate;
        return new Result(pcm, sampleRate, speechMs);
    }
}
