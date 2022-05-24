package com.neucore.neulink.impl.cmd.faceupld.v12;

import com.google.gson.annotations.SerializedName;

public class RecogInfo12 {
    @SerializedName("recognized")
    private int recognized;
    @SerializedName("similarity_value")
    private String similarityValue;

    @SerializedName("db_id")
    private String dbId;
    @SerializedName("ext_id")
    private String extId;

    @SerializedName("level")
    private int level;
    @SerializedName("name")
    private String name;
    @SerializedName("gender")
    private String gender;
    @SerializedName("birthday")
    private String birthday;

    @SerializedName("feature_id")
    private String featureId;

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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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

    public String getFeatureId() {
        return featureId;
    }

    public void setFeatureId(String featureId) {
        this.featureId = featureId;
    }
}
