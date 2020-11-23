package com.luoye.bzcamera.utils;

/**
 * Created by jack_liu on 2020-01-10 10:01.
 * description:
 */
public enum CameraCapacityCheckResult {
    GOOD, ERROR, NOT_SUPPORT_CAMERA2, MAX_PIXEL_LESS,
    ISO_ADJUSTABLE_RANGE_UNAVAILABLE, ISO_ADJUST_UNAVAILABLE,
    HARDWARE_LEVEL_LESS, NOT_ENOUGH_FPS
}
