package com.neucore.neusdk_demo.neulink.extend;

import com.neucore.neulink.IStorage;
import com.neucore.neulink.extend.StorageFactory;
import com.neucore.neulink.faceupld.AIData;
import com.neucore.neulink.faceupld.AtrrInfo;
import com.neucore.neulink.faceupld.DetectInfo;
import com.neucore.neulink.faceupld.Face;
import com.neucore.neulink.faceupld.FaceInfo;
import com.neucore.neulink.faceupld.FaceUpload;
import com.neucore.neulink.faceupld.RecogInfo;
import com.neucore.neulink.impl.NeulinkPublisherFacde;
import com.neucore.neulink.impl.NeulinkService;

import java.util.UUID;

/**
 * 人脸上报
 */
public class SampleFaceUpload {
    /**
     * v1.0
     * @deprecated
     */
    public void v10sample(){

        String requestId = UUID.randomUUID().toString();
        int index = 0;

        String path = "/sdcard/twocamera/icon/1593399670069.jpg";//待上传的图片路径
        //图片上传【FTP】
        //调整配置Storage.Type=FTP
        IStorage storage = StorageFactory.getInstance();//目前只实现了OSS|FTP[]
        //上传人脸图片至存储服务器上
        String urlStr = storage.uploadImage(path,requestId,index);//返回图片FTP|OSS路径

        //上报消息前，需要把抓拍到的照片放入的OSS服务中[特别重要]
        NeulinkService service = NeulinkService.getInstance();
        NeulinkPublisherFacde publisher = service.getPublisherFacde();

        FaceInfo faceDetect = new FaceInfo();
        //设置抓拍到的时间点unixtime_stamp
        faceDetect.setTimestamp(12345);
        //类型: 1 检测 2 检测+识别 3 检测+人脸属性
        faceDetect.setType(1);
        //检测对象信息f
        DetectInfo detectInfo = new DetectInfo();

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

        publisher.upldFaceInfo(urlStr,faceDetect);
    }

    /**
     * v1.1
     */
    public void v11sample(){

        String requestId = UUID.randomUUID().toString();
        int index = 0;

        String path = "/sdcard/twocamera/icon/1593399670069.jpg";//待上传的图片路径
        //图片上传【FTP】
        //调整配置Storage.Type=FTP
        IStorage storage = StorageFactory.getInstance();//目前只实现了OSS|FTP[]
        //上传人脸图片至存储服务器上
        String urlStr = storage.uploadImage(path,requestId,index);//返回图片FTP|OSS路径

        //上报消息前，需要把抓拍到的照片放入的OSS服务中[特别重要]
        NeulinkService service = NeulinkService.getInstance();
        NeulinkPublisherFacde publisher = service.getPublisherFacde();

        FaceUpload faceUpload = new FaceUpload();
        //设置抓拍到的时间点unixtime_stamp
        faceUpload.setTimestamp(12345);
        //类型: 1 检测 2 检测+识别 3 检测+人脸属性
        faceUpload.setType(1);
        //检测对象信息f
        DetectInfo detectInfo = new DetectInfo();

        //调用算法得到的人脸id
        detectInfo.setFaceId("0");

        AIData aiData = new AIData();

        /**
         * 看有几张人脸：这里只例举一张人脸的情况
         */
        Face[] faceArray = new Face[1];

        Face face = new Face();

        //人脸编号，设备端唯一
        face.setFaceNo("111");
        //发送的人脸图片的名称   [可选]
        face.setFaceImg("face.jpg");
        //发送的face_id的文件名[可选]
        face.setFeatureId("1");

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
        //性别
        recogInfo.setGender("0");
        //生日
        recogInfo.setBirthday("202005221");
        face.setRecogInfo(recogInfo);

        //人脸属性对象
        AtrrInfo atrrInfo = new AtrrInfo();
        //性别
        atrrInfo.setGender(0);
        //是否戴帽子
        atrrInfo.setHasHat(0);
        //是否侧脸
        atrrInfo.setSlideFace(0);
        //是否低头
        atrrInfo.setLowerHead(0);
        //太阳眼睛
        atrrInfo.setSunglasses(0);
        //面具
        atrrInfo.setMask(0);
        //年龄
        atrrInfo.setAge(0);
        //情绪
        atrrInfo.setEmotion(0);

        face.setAtrrInfo(atrrInfo);

        faceArray[0] = face;

        aiData.setFaces(faceArray);

        faceUpload.setAiData(aiData);

        publisher.upldFaceInfo(urlStr,faceUpload);
    }
}
