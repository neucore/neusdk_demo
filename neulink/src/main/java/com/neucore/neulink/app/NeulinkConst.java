package com.neucore.neulink.app;

import android.os.Environment;

public interface NeulinkConst {
    String TAG_PREFIX = "Neulink";
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

    String Backup_Obj_Cfg = "cfg";

    String Backup_Obj_Syscfg = "syscfg";

    String Backup_Obj_Data = "data";

    String V1$0 = "v1.0";

    String V1$1 = "v1.1";

    String V1$2 = "v1.2";
}
