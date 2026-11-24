package com.neucore.voicesdk.internal;

import android.util.Log;

/**
 * 统一日志。关闭 debug 后只输出 warn/error。
 */
public final class VoiceLog {

    private static final String TAG = "VoiceSdk";
    private static volatile boolean debug = false;

    private VoiceLog() {
    }

    public static void setDebug(boolean enable) {
        debug = enable;
    }

    public static void d(String msg) {
        if (debug) {
            Log.d(TAG, msg);
        }
    }

    public static void i(String msg) {
        Log.i(TAG, msg);
    }

    public static void w(String msg) {
        Log.w(TAG, msg);
    }

    public static void e(String msg, Throwable t) {
        Log.e(TAG, msg, t);
    }
}
