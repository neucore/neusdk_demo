package com.neucore.neulink.impl;

import com.neucore.neulink.impl.registry.ServiceRegistry;

public class NeulinkTopicParser {

    //msg/req/devinfo/v1.0/${req_no}/[/${md5}]
    //rmsg/req/${dev_id}/sys_ctrl/v1.0/${req_no}/[/${md5}]
    //rrpc/req/${dev_id}/facelib/v1.0/${req_no}[/${md5}
    //upld/res/${dev_id}/carplateinfo/v1.0/${req_no}[/${md5}], qos=0
    //upld/res/${dev_id}/facetemprature/v1.0/${req_no}[/${md5}], qos=0
    private static NeulinkTopicParser parser = new NeulinkTopicParser();
    public static NeulinkTopicParser getInstance(){
        return parser;
    }
    public Topic parser(String topStr,int qos){
        Topic topic = new Topic();
        topic.setString(topStr);
        String[] paths = topStr.split("/");
        int len = paths.length;
        String prefix = paths[0];

        topic.setPrefix(prefix);
        topic.setReq$res(paths[1]);
        if(topStr.startsWith("bcst/")){
            /**
             * 广播设备
             */
            topic.setBiz(paths[3]);
            topic.setVersion(paths[4]);
            topic.setReqId(paths[5]);
            if(len>6){
                topic.setMd5(paths[6]);
            }
            topic.setQos(qos);
        }
        else{
            /**
             * 单台设备
             */
            //msg/req/devinfo/v1.0/${req_no}/[/${md5}]
            if("msg".equalsIgnoreCase(prefix)) {
                topic.setBiz(paths[2]);
                topic.setVersion(paths[3]);
                topic.setReqId(paths[3]);
                if(len>5){
                    topic.setMd5(paths[5]);
                }
            }
            else{
                //rmsg/req/${dev_id}/sys_ctrl/v1.0/${req_no}/[/${md5}]
                //rrpc/req/${dev_id}/facelib/v1.0/${req_no}[/${md5}
                //upld/res/${dev_id}/carplateinfo/v1.0/${req_no}[/${md5}], qos=0
                //upld/res/${dev_id}/facetemprature/v1.0/${req_no}[/${md5}], qos=0
                topic.setBiz(paths[3]);
                topic.setVersion(paths[4]);
                topic.setReqId(paths[5]);
                if(len>6){
                    topic.setMd5(paths[6]);
                }
                topic.setQos(qos);
            }
        }
        return topic;
    }

    public static class Topic{
        private String prefix;
        private String req$res;
        private String reqId;
        private String md5;
        private String biz;
        private String devId;
        private String version;
        private String topString;
        private int qos;

        public Topic(){
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getReq$res() {
            return req$res;
        }

        public void setReq$res(String req$res) {
            this.req$res = req$res;
        }

        public String getReqId() {
            return reqId;
        }

        public void setReqId(String reqId) {
            this.reqId = reqId;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public String getBiz() {
            return biz;
        }

        public void setBiz(String biz) {
            this.biz = biz;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public void setString(String topString) {
            this.topString = topString;
        }

        public String toString(){
            return topString;
        }

        public int getQos() {
            return qos;
        }

        public void setQos(int qos) {
            this.qos = qos;
        }
    };
}
