package com.neucore.neusdk_demo.neulink.extend.file.request;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.Cmd;

/**
 * 设备添加文件请求接收
 */
public class FileSyncCmd extends Cmd {

    @SerializedName("id")
    String id;

    @SerializedName("name")
    private String name; //name

    @SerializedName("type")
    private String type; //1:图片 2:视频 3:音频 4:文字消息

    @SerializedName("size")
    private long size; //文件大小

    @SerializedName("uid")
    private int uid; //用户数据库ID

    @SerializedName("url")
    private String url; //文件地址

    @SerializedName("lng")
    private String lng; //经度

    @SerializedName("lat")
    private String lat; //纬度

    @SerializedName("ctime")
    private long ctime;

    @SerializedName("time_stamp")
    private long time_stamp;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public long getCtime() {
        return ctime;
    }

    public void setCtime(long ctime) {
        this.ctime = ctime;
    }

    public long getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(long time_stamp) {
        this.time_stamp = time_stamp;
    }
}

