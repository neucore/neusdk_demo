package com.neucore.neulink.impl.cmd.lic;

import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.impl.ArgCmd;

public class LicUpldCmd extends ArgCmd {

    @SerializedName("dev_id")
    private String deviceId;
    @SerializedName("carplate_num")
    private String num;
    @SerializedName("carplate_color")
    private String color;
    @SerializedName("carplate_image_url")
    private String imageUrl;

    @SerializedName("company_code")
    private String cmpCode;

    @SerializedName("location_code")
    private String locationCode;

    @SerializedName("position")
    private String position;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCmpCode() {
        return cmpCode;
    }

    public void setCmpCode(String cmpCode) {
        this.cmpCode = cmpCode;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
