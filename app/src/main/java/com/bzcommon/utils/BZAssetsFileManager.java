package com.bzcommon.utils;

import android.content.Context;

import com.blankj.utilcode.util.LogUtils;

import java.io.File;

/**
 * Created by jack_liu on 2018-12-28 16:32.
 * 说明:缓存资产目录下的文件
 */
public class BZAssetsFileManager {
    private static final String TAG = "bz_AssetsFileManager";

    public static String getFinalPath(Context context, String path) {
        if (null == context || BZStringUtils.isEmpty(path)) {
            return path;
        }
        if (path.startsWith("/")) {
            return path;
        }
        //处理资产目录下的文件
        try {
            String fileDirPath = context.getFilesDir().getAbsolutePath();
            String finalPath = fileDirPath + "/" + path;
            if (new File(finalPath).exists()) {
                return finalPath;
            }
            BZFileUtils.createNewFile(finalPath);

            BZFileUtils.fileCopy(context.getAssets().open(path), finalPath);
            return finalPath;
        } catch (Throwable e) {
            LogUtils.eTag(TAG, e);
        }
        return path;
    }
}
