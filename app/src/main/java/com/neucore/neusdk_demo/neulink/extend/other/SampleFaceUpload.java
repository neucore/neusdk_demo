package com.neucore.neusdk_demo.neulink.extend.other;

import com.neucore.neulink.IStorage;
import com.neucore.neulink.impl.cmd.faceupld.v12.AIData12;
import com.neucore.neulink.impl.cmd.faceupld.v12.AtrrInfo12;
import com.neucore.neulink.impl.cmd.faceupld.v12.DetectInfo12;
import com.neucore.neulink.impl.cmd.faceupld.v12.Face12;
import com.neucore.neulink.impl.cmd.faceupld.v12.FaceRect12;
import com.neucore.neulink.impl.cmd.faceupld.v12.FaceUpload12;
import com.neucore.neulink.impl.cmd.faceupld.v12.Keypoints;
import com.neucore.neulink.impl.cmd.faceupld.v12.KeypointsInfo12;
import com.neucore.neulink.impl.cmd.faceupld.v12.RecogInfo12;
import com.neucore.neulink.impl.cmd.faceupld.v12.RrInfo12;
import com.neucore.neulink.impl.StorageFactory;
import com.neucore.neulink.impl.NeulinkPublisherFacde;
import com.neucore.neulink.impl.NeulinkService;

import java.util.UUID;

/**
 * 人脸上报
 */
public class SampleFaceUpload {

    /**
     *
     * 最新协议
     */
    public void v12sample(){
        String requestId = UUID.randomUUID().toString();
        int index = 0;

        String path = "/sdcard/twocamera/572836928.jpg";//待上传的图片路径
        //图片上传【FTP】
        //调整配置Storage.Type=FTP
        /**
         *
         */
        IStorage storage = StorageFactory.getInstance();//目前只实现了【OSS｜FTP】
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
