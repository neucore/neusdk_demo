package com.bzcommon.glutils;


import com.bzcommon.utils.BZLogUtil;

import static android.opengl.GLES20.GL_NO_ERROR;
import static android.opengl.GLES20.glGetError;

/**
 * Created by jack_liu on 2018-01-05 14:05.
 * 说明:
 */

public class GLUtil {
    private static final String TAG = "bz_GLUtil";

    public static void checkGlError(String tag) {
        int error;
        while ((error = glGetError()) != GL_NO_ERROR) {
            BZLogUtil.e(TAG, "GL ERROR " + tag + " glError=" + error);
        }
    }
}
