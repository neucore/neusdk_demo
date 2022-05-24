package com.neucore.neulink.impl.cmd.faceupld;

import com.google.gson.annotations.SerializedName;

@Deprecated
public class RecogInfo {
    @SerializedName("recognized")
    private int recognized;
    @SerializedName("similarity_value")
    private String similarityValue;
    @SerializedName("similarity_threshold")
    private String similarityThreshold;
    @SerializedName("db_id")
    private String dbId;
    @SerializedName("ext_id")
    private String extId;
    @SerializedName("name")
    private String name;
    @SerializedName("gender")
    private String gender;
    @SerializedName("birthday")
    private String birthday;

    public int getRecognized() {
        return recognized;
    }

    public void setRecognized(int recognized) {
        this.recognized = recognized;
    }

    public String getSimilarityValue() {
        return similarityValue;
    }

    public void setSimilarityValue(String similarityValue) {
        this.similarityValue = similarityValue;
    }

    public String getSimilarityThreshold() {
        return similarityThreshold;
    }

    public void setSimilarityThreshold(String similarityThreshold) {
        this.similarityThreshold = similarityThreshold;
    }

    public String getDbId() {
        return dbId;
    }

    public void setDbId(String dbId) {
        this.dbId = dbId;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
