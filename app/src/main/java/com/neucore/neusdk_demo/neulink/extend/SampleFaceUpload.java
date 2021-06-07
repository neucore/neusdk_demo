package com.neucore.neusdk_demo.neulink.extend;

import android.util.Log;

import com.neucore.neulink.IStorage;
import com.neucore.neulink.extend.StorageFactory;
import com.neucore.neulink.faceupld.AIData;
import com.neucore.neulink.faceupld.AtrrInfo;
import com.neucore.neulink.faceupld.DetectInfo;
import com.neucore.neulink.faceupld.Face;
import com.neucore.neulink.faceupld.FaceInfo;
import com.neucore.neulink.faceupld.FaceUpload;
import com.neucore.neulink.faceupld.RecogInfo;
import com.neucore.neulink.faceupld.v12.AIData12;
import com.neucore.neulink.faceupld.v12.AtrrInfo12;
import com.neucore.neulink.faceupld.v12.DetectInfo12;
import com.neucore.neulink.faceupld.v12.Face12;
import com.neucore.neulink.faceupld.v12.FaceRect12;
import com.neucore.neulink.faceupld.v12.FaceUpload12;
import com.neucore.neulink.faceupld.v12.Keypoints;
import com.neucore.neulink.faceupld.v12.KeypointsInfo12;
import com.neucore.neulink.faceupld.v12.RecogInfo12;
import com.neucore.neulink.faceupld.v12.RrInfo12;
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

        //图片上传【FTP】
        //调整配置Storage.Type=FTP
        IStorage storage = StorageFactory.getInstance();//目前只实现了OSS|FTP[]
        if(storage==null){
            Log.e("Upload","存储对象创建失败");
            return;
        }
        String requestId = UUID.randomUUID().toString();
        int index = 0;
        String path = "/sdcard/twocamera/icon/1593399670069.jpg";//待上传的图片路径
        //上传人脸图片至存储服务器上
        String urlStr = storage.uploadImage(path,requestId,index);//返回图片FTP|OSS路径
        if(urlStr!=null){
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
    }

    /**
     * v1.1
     * @deprecated
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
        if(urlStr!=null){
            //上报消息前，需要把抓拍到的照片放入的OSS服务中[特别重要]
            NeulinkService service = NeulinkService.getInstance();
            NeulinkPublisherFacde publisher = service.getPublisherFacde();

            FaceUpload faceUpload = new FaceUpload();
            //设置抓拍到的时间点unixtime_stamp
            faceUpload.setTimestamp(12345);
            //类型: 1 检测 2 检测+识别 3 检测+人脸属性
            faceUpload.setType(1);
            /**
             * 临时访客不在有效期进出时进行提醒。。。
             */
            faceUpload.setAttachInfo("失效访客");
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

    /**
     *
     * 最新协议
     */
    public void v12sample(){
        String requestId = UUID.randomUUID().toString();
        int index = 0;

        String path = "/sdcard/twocamera/icon/1593399670069.jpg";//待上传的图片路径
        //图片上传【FTP】
        //调整配置Storage.Type=FTP
        IStorage storage = StorageFactory.getInstance();//目前只实现了OSS|FTP[]
        //上传人脸图片至存储服务器上
        String urlStr = storage.uploadImage(path,requestId,index);//返回图片FTP|OSS路径
        if(urlStr!=null){
            FaceUpload12 faceUpload12 = new FaceUpload12();

            faceUpload12.setPosition("In01");
            faceUpload12.setChannel(0);
            faceUpload12.setTimestamp(1592309303);
            faceUpload12.setModule("face");
            faceUpload12.setType(1);

            AIData12 aiData12 = new AIData12();
            aiData12.setDir("290b1000010e1200000337304e424e50/20200624/073828_71");
            aiData12.setBackground("background.jpg");

            aiData12.setDetectFlag(1);
            aiData12.setAtrrFlag(1);
            aiData12.setKeypointsFlag(1);
            aiData12.setRrFlag(1);
            aiData12.setRecogFlag(1);
            aiData12.setLive_flag(1);

            int faceNum = 1;
            aiData12.setFaceNum(faceNum);

            Face12[] face12s = new Face12[faceNum];

            for (int i=0;i<faceNum;i++) {
                /**
                 * 根据faceNum
                 */
                face12s[i] = new Face12();

                face12s[i].setHeadImg("head_1.jpg");

                DetectInfo12 detectInfo12 = new DetectInfo12();

                FaceRect12 faceRect12 = new FaceRect12();

                faceRect12.setPointX(1);
                faceRect12.setPointX(10);
                faceRect12.setWidth(100);
                faceRect12.setHeight(100);

                detectInfo12.setFaceRect12(faceRect12);
                detectInfo12.setIsAlive(1);
                detectInfo12.setTrackId(1);

                face12s[i].setDetectInfo(detectInfo12);


                RecogInfo12 recogInfo12 = new RecogInfo12();
                recogInfo12.setRecognized(1);
                recogInfo12.setSimilarityValue("0.5736");
                recogInfo12.setDbId("72");
                recogInfo12.setExtId("1,44034704");//人脸下发时的exitId
                recogInfo12.setLevel(1);
                recogInfo12.setName("aoqi");
                recogInfo12.setGender("0");
                recogInfo12.setBirthday("202005221");
                recogInfo12.setFeatureId("face_id.bin");

                face12s[i].setRecogInfo(recogInfo12);

                KeypointsInfo12 keypointsInfo12 = new KeypointsInfo12();
                Keypoints keypoints = new Keypoints();
                keypoints.setKeyX(new int[]{1,2,3,4,5});
                keypoints.setKeyY(new int[]{1,2,3,4,5});
                keypointsInfo12.setKeypoints(keypoints);

                face12s[i].setKeypointsInfo(keypointsInfo12);

                AtrrInfo12 atrrInfo12 = new AtrrInfo12();
                atrrInfo12.setIsValid(0);
                atrrInfo12.setGender(0);
                atrrInfo12.setHasHat(0);
                atrrInfo12.setSlideFace(0);
                atrrInfo12.setLowerHead(0);
                atrrInfo12.setSunglasses(0);
                atrrInfo12.setMask(0);
                atrrInfo12.setAge(0);
                atrrInfo12.setEmotion(0);

                face12s[i].setAtrrInfo(atrrInfo12);

                RrInfo12 rrInfo12 = new RrInfo12();
                rrInfo12.setFaceNo(123456);
                rrInfo12.setPoseScore(0.5f);
                rrInfo12.setClearScore(0.6f);
                face12s[i].setRrInfo(rrInfo12);
            }

            aiData12.setFaces(face12s);

            faceUpload12.setAiData(aiData12);

            //上报消息前，需要把抓拍到的照片放入的OSS服务中[特别重要]
            NeulinkService service = NeulinkService.getInstance();
            NeulinkPublisherFacde publisher = service.getPublisherFacde();
            publisher.upldFaceInfo$1$2(urlStr,faceUpload12);
        }
    }
}
