package com.neucore.neulink.impl;

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

    public Topic cloud2EndParser(String topStr){
        Topic topic = new Topic();
        topic.setString(topStr);
        String[] paths = topStr.split("/");
        boolean hasProduct = hasProduct(paths);
        int len = paths.length;
        if(hasProduct){
            topic.setProduct(paths[0]);
            String group = paths[1];
            topic.setGroup(group);
            topic.setReq$res(paths[2]);
            if(topStr.startsWith("bcst/")){
                /**
                 * 广播设备
                 */
                if(len>4){
                    topic.setVersion(paths[4]);
                }
            }
            else{
                /**
                 * 单播设备
                 * rmsg/req/${dev_id}/sys_ctrl/v1.0/${req_no}/[/${md5}]
                 * rrpc/req/${dev_id}/alog/v1.0/${req_no}[/${md5}], qos=0
                 * upld/res/${dev_id}/carplateinfo/v1.0/${req_no}[/${md5}], qos=0
                 * bcst/req/${dev_id}/alog/v1.0/${req_no}[/${md5}]
                 */
                if(len>7){
                    /**
                     * 兼容老版本
                     */
                    if(len>4){
                        topic.setBiz(paths[4]);
                    }
                    if(len>5){
                        topic.setVersion(paths[5]);
                    }
                    if(len>6) {
                        topic.setReqId(paths[6]);
                    }
                    if(len>7){
                        topic.setMd5(paths[7]);
                    }
                }
                else if(len>6){
                    /**
                     * 新版本
                     */
                    if(len>4){
                        topic.setVersion(paths[4]);
                    }
                    if(len>5){
                        topic.setReqId(paths[5]);
                    }
                    if(len>6){
                        topic.setVersion(paths[6]);
                    }
                }
            }
        }
        else{
            String group = paths[0];
            topic.setGroup(group);
            topic.setReq$res(paths[1]);
            if(topStr.startsWith("bcst/")){
                /**
                 * 广播设备
                 */
                if(len>3){
                    topic.setVersion(paths[3]);
                }
            }
            else{
                /**
                 * 单播设备
                 * rmsg/req/${dev_id}/sys_ctrl/v1.0/${req_no}/[/${md5}]
                 * rrpc/req/${dev_id}/alog/v1.0/${req_no}[/${md5}], qos=0
                 * upld/res/${dev_id}/carplateinfo/v1.0/${req_no}[/${md5}], qos=0
                 * bcst/req/${dev_id}/alog/v1.0/${req_no}[/${md5}]
                 */
                if(len>6){
                    /**
                     * 兼容老版本
                     */
                    if(len>3){
                        topic.setBiz(paths[3]);
                    }
                    if(len>4){
                        topic.setVersion(paths[4]);
                    }
                    if(len>5) {
                        topic.setReqId(paths[5]);
                    }
                    if(len>6){
                        topic.setMd5(paths[6]);
                    }
                }
                else if(len>5){
                    /**
                     * 新版本
                     */
                    if(len>3){
                        topic.setVersion(paths[3]);
                    }
                    if(len>4){
                        topic.setReqId(paths[4]);
                    }
                    if(len>5){
                        topic.setVersion(paths[5]);
                    }
                }
            }
        }
        return topic;
    }

    /**
     * 【msg｜upld】/req/[devinfo|status|faceinfo|...]/vx.x/${req_no}/${md5}[/$custid}][/${storeid}][/${zoneid}][/${dev_id}]
     * @param topStr
     * @param qos
     * @return
     */
    public Topic end2cloudParser(String topStr, int qos){
        Topic topic = new Topic();
        topic.setString(topStr);
        String[] paths = topStr.split("/");
        boolean hasProduct = hasProduct(paths);
        int len = paths.length;
        if(hasProduct){
            topic.setProduct(paths[0]);
            String group = paths[1];
            topic.setGroup(group);
            topic.setReq$res(paths[2]);

            if(len>3){
                topic.biz = paths[3];
            }
            if(len>4){
                topic.version = paths[4];
            }
            if(len>5){
                topic.reqId = paths[5];
            }
            if(len>6){
                topic.md5 = paths[6];
            }
        }
        else{
            String group = paths[0];
            topic.setGroup(group);
            topic.setReq$res(paths[1]);

            if(len>2){
                topic.biz = paths[2];
            }
            if(len>3){
                topic.version = paths[3];
            }
            if(len>4){
                topic.reqId = paths[4];
            }
            if(len>5){
                topic.md5 = paths[5];
            }
        }

        return topic;
    }

    public static class Topic{
        private String product;
        private String group;
        private String req$res;
        private String reqId;
        private String md5;
        private String biz;
        private String version;
        private String topString;
        private int qos;

        public Topic(){
        }

        public String getProduct() {
            return product;
        }

        public void setProduct(String product) {
            this.product = product;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
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
    };

    private static boolean hasProduct(String[] topicA){
        if("rmsg".equalsIgnoreCase(topicA[0])
                || "rrpc".equalsIgnoreCase(topicA[0])
                || "msg".equalsIgnoreCase(topicA[0])
                || "upld".equalsIgnoreCase(topicA[0])
                || "bcst".equalsIgnoreCase(topicA[0])
                || "svc".equalsIgnoreCase(topicA[0])
        ){
            return false;
        }
        else if("rmsg".equalsIgnoreCase(topicA[1])
                || "rrpc".equalsIgnoreCase(topicA[1])
                || "msg".equalsIgnoreCase(topicA[1])
                || "upld".equalsIgnoreCase(topicA[1])
                || "bcst".equalsIgnoreCase(topicA[1])
                || "svc".equalsIgnoreCase(topicA[1])){
            return true;
        }
        return false;
    }
}
