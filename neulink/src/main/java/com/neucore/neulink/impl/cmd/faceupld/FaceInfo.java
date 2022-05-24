package com.neucore.neulink.impl.cmd.faceupld;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.ArgCmd;

/**
 *
 */
@Deprecated
public class FaceInfo extends ArgCmd {

    @SerializedName("dev_id")
    private String deviceId;

    @SerializedName("channel")
    private int channel = 0;

    @SerializedName("time_stamp")
    private long timestamp;

    @SerializedName("type")
    private int type;


    @SerializedName("detect_info")
    private DetectInfo detectInfo;

    @SerializedName("recog_info")
    private RecogInfo recogInfo;

    @SerializedName("atrr_info")
    private AtrrInfo atrrInfo;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public DetectInfo getDetectInfo() {
        return detectInfo;
    }

    public void setDetectInfo(DetectInfo detectInfo) {
        this.detectInfo = detectInfo;
    }

    public RecogInfo getRecogInfo() {
        return recogInfo;
    }

    public void setRecogInfo(RecogInfo recogInfo) {
        this.recogInfo = recogInfo;
    }

    public AtrrInfo getAtrrInfo() {
        return atrrInfo;
    }

    public void setAtrrInfo(AtrrInfo atrrInfo) {
        this.atrrInfo = atrrInfo;
    }

    private void test(){

        FaceInfo faceDetect = new FaceInfo();
        //设置抓拍到的时间点unixtime_stamp
        faceDetect.setTimestamp(12345);
        //类型: 1 检测  2 检测+识别 3 检测+人脸属性
        faceDetect.setType(1);
        //检测对象信息
        DetectInfo detectInfo = new DetectInfo();
        //发送的人脸图片/face_id/背景图的地址 oss存储的路径【不包括EndPoint内容】
        detectInfo.setDir("290b1000010e1200000337304e424e50/20200624/073828_71");
        //人脸照片文件名
        detectInfo.setFaceImage("face_0328.jpg");

        //调用算法得到的人脸id
        detectInfo.setFaceId("0");

        faceDetect.setDetectInfo(detectInfo);

        //识别对象信息【可选，识别出来的需要】
        RecogInfo recogInfo = new RecogInfo();
        //是否识别 0:未识别；1：已识别
        recogInfo.setRecognized(1);
        //该人脸和数据中所有条目进行对比得到的最大相似度
        recogInfo.setSimilarityValue("0.5736");
        //人脸相似度阈值
        recogInfo.setSimilarityThreshold("0.5");
        //卡号
        recogInfo.setExtId("434343243");
        //姓名
        recogInfo.setName("张三");
        //识别对象
        faceDetect.setRecogInfo(recogInfo);

    }
}
