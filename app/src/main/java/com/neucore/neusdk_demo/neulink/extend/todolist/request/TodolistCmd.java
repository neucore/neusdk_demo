package com.neucore.neusdk_demo.neulink.extend.todolist.request;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.Cmd;

/**
 * 设备添加文件请求接收
 */
public class TodolistCmd extends Cmd {

    @SerializedName("id")
    String id;

    @SerializedName("title")
    private String title; //name

    @SerializedName("desc")
    private String desc; //描述

    @SerializedName("type")
    private int type; //类型

    @SerializedName("url")
    private String url; //文件地址

    @SerializedName("imgs")
    private String[] imgs; //图片文件地址

    @SerializedName("audios")
    private String[] audios; //音频文件地址

    @SerializedName("ringBell")
    private int ringBell; //1-铃声1；2-铃声2；3-铃声3；4-铃声4；5-铃声5；6-铃声6；7-铃声7；...

    @SerializedName("sunriseEffect")
    private int sunriseEffect; //日出效果开关: 0-关闭; 1-打开

    @SerializedName("noticeType")
    private String noticeType;//提醒时间类型； 0:日程开始时；1:5分钟前；2:10分钟前；3:15分钟前；4:30分钟前；5:一小时前；6:一天前；7:两天前；8:一周前 9: 日程当天 9 点；10: 日程前一天 9 点；11: 日程两天前 9 点

    @SerializedName("startDate")
    private String startDate;//开始日期

    @SerializedName("startTime")
    private String startTime;//开始时间

    @SerializedName("endDate")
    private String endDate;//开始日期

    @SerializedName("endTime")
    private String endTime;//开始时间


    @SerializedName("repeat")
    private String repeat;//重复类型；0:永不；1:每天；2:每周；3:每月；4:每年

    @SerializedName("timesheet")
    private String timesheet;//多值用';'分割

    @SerializedName("repeatEndType")
    private String repeatEndType;//重复结束类型；0:永不结束；1:与日期

    @SerializedName("repeatEnd")
    private String repeatEnd;//重复结束时间


    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String[] getImgs() {
        return imgs;
    }

    public void setImgs(String[] imgs) {
        this.imgs = imgs;
    }

    public String[] getAudios() {
        return audios;
    }

    public void setAudios(String[] audios) {
        this.audios = audios;
    }

    public int getRingBell() {
        return ringBell;
    }

    public void setRingBell(int ringBell) {
        this.ringBell = ringBell;
    }

    public int getSunriseEffect() {
        return sunriseEffect;
    }

    public void setSunriseEffect(int sunriseEffect) {
        this.sunriseEffect = sunriseEffect;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getTimesheet() {
        return timesheet;
    }

    public void setTimesheet(String timesheet) {
        this.timesheet = timesheet;
    }

    public String getRepeatEndType() {
        return repeatEndType;
    }

    public void setRepeatEndType(String repeatEndType) {
        this.repeatEndType = repeatEndType;
    }

    public String getRepeatEnd() {
        return repeatEnd;
    }

    public void setRepeatEnd(String repeatEnd) {
        this.repeatEnd = repeatEnd;
    }
}

