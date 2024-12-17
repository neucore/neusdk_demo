package com.neucore.neulink.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class AppUtils {

    /**
     * 获取当前apk的版本号
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        PackageInfo packInfo =null;
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        }
        catch (Exception ex){
        }
        return packInfo.versionCode;
    }

    public static String getApkName(Context context){
        ApplicationInfo appInfo;
        String appName = "";

        try {

            appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);

            appName = appInfo.loadLabel(context.getPackageManager()) + "";

            Log.d("稳定、可靠获取App名称", appName);

        } catch (Exception e) {

            e.printStackTrace();

        }
        return appName;
    }

    /**
     * 得到当前应用版本名称的方法
     *
     * @param context
     *            :上下文
     * @throws Exception
     */
    public static String getVersionName(Context context) {
        PackageInfo packInfo =null;
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        }
        catch (Exception ex){
        }
        String version = packInfo.versionName;
        return version;
    }
}
