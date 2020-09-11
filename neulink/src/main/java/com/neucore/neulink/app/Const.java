package com.neucore.neulink.app;

import android.os.Environment;

public class Const {
    public static String filePath= Environment.getExternalStorageDirectory() + "/twocamera/";
    public static String picPath= Environment.getExternalStorageDirectory() + "/twocamera/icon/";//头像
    public static String photoPath= Environment.getExternalStorageDirectory() + "/twocamera/photo/";//抓拍
    public static String fileExport= Environment.getExternalStorageDirectory() + "/twocamera/export/";//导出

    public static String yanzheng_type="yanzheng_type";//主机认证类型
    public static String fanqianhui="fanqianhui";//反潜回
    public static String chaoci="chaoci";//认证超次报警
    public static String chaoci_num="chaoci_num";//认证做多次数

    public static final String LOG_CARSH = ".carsh";

    public static final String LOG_LOG = ".log";
}
