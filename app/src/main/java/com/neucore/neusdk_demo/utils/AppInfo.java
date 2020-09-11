package com.neucore.neusdk_demo.utils;

public class AppInfo {

    private static boolean startKCF = false; //默认关闭

    private static int widthPingMu = 800;

    private static int heightPingMu = 1232;


    public static void clear(){
        startKCF = false;
        widthPingMu = 800;
        heightPingMu = 1232;
    }

    public static boolean getStartKCF() {
        return startKCF;
    }

    public static void setStartKCF(boolean start) {
        AppInfo.startKCF = start;
    }

    public static int getWidthPingMu() {
        return widthPingMu;
    }

    public static void setWidthPingMu(int width) {
        AppInfo.widthPingMu = width;
    }

    public static int getHeightPingMu() {
        return heightPingMu;
    }

    public static void setHeightPingMu(int height) {
        AppInfo.heightPingMu = height;
    }




}
