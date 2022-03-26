package com.neucore.neusdk_demo.neulink.extend.file.listener;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.extend.ActionResult;

/**
 * 绑定返回的数据结果
 */
public class FileActionResult extends ActionResult {

    @SerializedName("mode")
    String mode;

    @SerializedName("id")
    String id;

    @SerializedName("name")
    private String name; //name

    @SerializedName("type")
    private String type; //1:图片 2:视频 3:音频 4:文字消息

    @SerializedName("size")
    private Long size; //文件大小

    @SerializedName("uid")
    private int uid; //用户数据库ID

    @SerializedName("url")
    private String url; //文件地址

    @SerializedName("lng")
    private String lng; //经度

    @SerializedName("lat")
    private String lat; //纬度

    @SerializedName("ctime")
    private Long ctime;

    @SerializedName("time_stamp")
    private Long time_stamp;
}
