package com.neucore.neulink.impl.cmd.rrpc;

import com.google.gson.annotations.SerializedName;

public class KVPair {

    public static enum  KeyEnum {
        // 1.定义枚举类型
        // 利用构造函数传参
        Type("type"),//人脸名单类型：value="1":黑名单
        PeriodStart("period_start"),//临时访客有效期限开始时间
        PeriodEnd("period_end");//临时访客有效期限开始时间
        // 定义私有变量
        private String key;
        // 构造函数，枚举类型只能为私有
        KeyEnum(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
        public String toString(){
            return key;
        }
    }

    @SerializedName("key")
    private String key;
    @SerializedName("value")
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
