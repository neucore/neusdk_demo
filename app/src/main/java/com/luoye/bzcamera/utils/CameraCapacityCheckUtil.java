package com.luoye.bzcamera.utils;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.util.Range;
import android.util.Size;

import androidx.annotation.RequiresApi;

import com.bzcommon.utils.BZLogUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jack_liu on 2020-01-10 09:48.
 * description:
 */
public class CameraCapacityCheckUtil {
    private static final String TAG = "bz_CameraCapacityCheckUtil";

    public static CameraCapacityCheckResult isSupportDFXSDK(Context context) {
        if (null == context) {
            BZLogUtil.e(TAG, "null==context");
            return CameraCapacityCheckResult.ERROR;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return CameraCapacityCheckResult.NOT_SUPPORT_CAMERA2;
        }
        return isSupportDFXSDK(context, CameraCharacteristics.LENS_FACING_FRONT);
    }

    public static CameraCapacityCheckResult isSupportDFXSDK(Context context, int lensFacing) {
        if (null == context || lensFacing < 0) {
            BZLogUtil.e(TAG, "null == context || cameraId < 0");
            return CameraCapacityCheckResult.ERROR;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return CameraCapacityCheckResult.NOT_SUPPORT_CAMERA2;
        }
        try {
            CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            String[] cameraIdList = manager.getCameraIdList();
            if (null == cameraIdList || cameraIdList.length <= 0) {
                return CameraCapacityCheckResult.ERROR;
            }
            CameraCharacteristics cameraCharacteristics = null;
            for (String cameraId : cameraIdList) {
                cameraCharacteristics = manager.getCameraCharacteristics(cameraId);
                Integer integer = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                if (null != integer && lensFacing == integer) {
                    break;
                }
            }
            if (null == cameraCharacteristics) {
                return CameraCapacityCheckResult.ERROR;
            }
            if (getCameraDeviceLevel(cameraCharacteristics) == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
                return CameraCapacityCheckResult.HARDWARE_LEVEL_LESS;
            }

            Range<Integer>[] fpsRanges = cameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
            BZLogUtil.d(TAG, "fpsRanges: " + Arrays.toString(fpsRanges));
            if (!isSupportFpsRange(fpsRanges)) {
                return CameraCapacityCheckResult.NOT_ENOUGH_FPS;
            }

            StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            long maxPixel = 0;
            if (null != streamConfigurationMap) {
                Size[] sizes = streamConfigurationMap.getOutputSizes(ImageFormat.YUV_420_888);
                if (null != sizes && sizes.length > 0) {
                    long tempPixel;
                    for (Size size : sizes) {
                        tempPixel = size.getHeight() * size.getWidth();
                        BZLogUtil.d(TAG, "previewSize width=" + size.getWidth() + " height=" + size.getHeight());
                        if (tempPixel > maxPixel) {
                            maxPixel = tempPixel;
                        }
                    }
                }
            }
            BZLogUtil.d(TAG, "maxPixel=" + maxPixel);
            if (maxPixel < 2000000) {
                return CameraCapacityCheckResult.MAX_PIXEL_LESS;
            }


            boolean enableAdjustIso = false;
            List<CaptureRequest.Key<?>> availableCaptureRequestKeys = cameraCharacteristics.getAvailableCaptureRequestKeys();
            for (CaptureRequest.Key<?> availableCaptureRequestKey : availableCaptureRequestKeys) {
                BZLogUtil.d(TAG, "availableCaptureRequestKey=" + availableCaptureRequestKey);
                if (CaptureRequest.SENSOR_SENSITIVITY.equals(availableCaptureRequestKey)) {
                    enableAdjustIso = true;
                }
            }
            if (!enableAdjustIso) {
                BZLogUtil.w(TAG, "!enableAdjustIso " + CameraCapacityCheckResult.ISO_ADJUST_UNAVAILABLE);
//                return CameraCapacityCheckResult.ISO_ADJUST_UNAVAILABLE;
            }

            Range<Integer> isoRange = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE);
            if (null != isoRange) {
                Integer isoUpper = isoRange.getUpper();
                Integer isoLower = isoRange.getLower();
                BZLogUtil.d(TAG, "isoRange: RangeUpper=" + isoUpper + " getLower=" + isoLower);
            } else {
                BZLogUtil.w("Can't get isoRange");
                return CameraCapacityCheckResult.ISO_ADJUSTABLE_RANGE_UNAVAILABLE;
            }
        } catch (Throwable e) {
            BZLogUtil.e(TAG, e);
            return CameraCapacityCheckResult.ERROR;
        }
        return CameraCapacityCheckResult.GOOD;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static int getCameraDeviceLevel(CameraCharacteristics characteristics) {
        Integer deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
        if (deviceLevel == null) {
            BZLogUtil.e(TAG, "can not get INFO_SUPPORTED_HARDWARE_LEVEL");
            return -1;
        }
        switch (deviceLevel) {
            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL:
                BZLogUtil.d(TAG, "hardware supported level:LEVEL_FULL");
                break;
            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY:
                BZLogUtil.w(TAG, "hardware supported level:LEVEL_LEGACY");
                break;
            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3:
                BZLogUtil.d(TAG, "hardware supported level:LEVEL_3");
                break;
            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED:
                BZLogUtil.d(TAG, "hardware supported level:LEVEL_LIMITED");
                break;
        }
        return deviceLevel;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static boolean isSupportFpsRange(Range<Integer>[] fpsRanges) {
        if (null == fpsRanges || fpsRanges.length <= 0) {
            return false;
        }
        boolean isSupport = false;
        for (Range<Integer> fpsRange : fpsRanges) {
            if (fpsRange.getLower() == 30 && fpsRange.getUpper() == 30) {
                isSupport = true;
                break;
            }
        }
        return isSupport;
    }
}
