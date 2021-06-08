package com.neucore.neulink.impl;

import android.content.Context;
import android.util.Log;

import com.neucore.neulink.IProcessor;
import com.neucore.neulink.faceupld.FaceInfo;
import com.neucore.neulink.faceupld.FaceUpload;
import com.neucore.neulink.faceupld.v12.FaceRect12;
import com.neucore.neulink.faceupld.v12.FaceUpload12;
import com.neucore.neulink.lic.LicUpldCmd;
import com.neucore.neulink.tmptr.FaceTemp;
import com.neucore.neulink.tmptr.FaceTempCmd;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.JSonUtils;

import cn.hutool.core.util.ObjectUtil;

/**
 * sdk应用信息上报接口：主要包括车牌上报、体温上报、人脸信息上报
 */
public class NeulinkPublisherFacde {

    private String TAG = "PublisherFacde";

    private static Context context;
    private static NeulinkService service;

    public NeulinkPublisherFacde(Context context, NeulinkService service) {
        this.context = context;
        this.service = service;
    }

    /**
     * 车牌上报
     * upld/req/carplateinfo/v1.0/${req_no}[/${md5}], qos=0
     */
    public void upldLic(String num,String color,String imageUrl,String cmpCode,String locationCode,String position){
        LicUpldCmd req = new LicUpldCmd();
        req.setNum(num);
        req.setColor(color);
        req.setImageUrl(imageUrl);
        req.setDeviceId(DeviceUtils.getDeviceId(context));
        req.setCmpCode(cmpCode);
        req.setLocationCode(locationCode);
        req.setPosition(position);

        String payload = JSonUtils.toString(req);
        String topic = "upld/req/carplateinfo";
        service.publishMessage(topic, IProcessor.V1$0, payload, 0);
    }

    /**
     * 体温上报
     * upld/req/facetemprature/v1.0/${req_no}[/${md5}], qos=0
     */
    public void upldFacetmp(FaceTemp[] data){
        FaceTempCmd req = new FaceTempCmd();
        req.setDeviceId(DeviceUtils.getDeviceId(context));
        req.setData(data);

        String payload = JSonUtils.toString(req);
        String topic = "upld/req/facetemprature";
        service.publishMessage(topic, IProcessor.V1$0, payload, 0);
    }

    /**
     * 人脸上报
     * 1.2版本协议
     * @param url 人脸照片的url
     * @param info 人脸识别信息
     */
    public void upldFaceInfo$1$2(String url, FaceUpload12 info){
        if(!ObjectUtil.isEmpty(url)){
            int index = url.lastIndexOf("/");
            if(index!=-1){
                String dir =url.substring(0,index);
                if(!ObjectUtil.isEmpty(dir)){
                    info.setDeviceId(DeviceUtils.getDeviceId(context));

                    if (info.getAiData()!=null){
                        info.getAiData().setDir(dir);
                    }

                    String payload = JSonUtils.toString(info);
                    String topic = "upld/req/faceinfo/"+info.getDeviceId();
                    service.publishMessage(topic, IProcessor.V1$2, payload, 0);
                }
                else{
                    Log.i(TAG,String.format("url=%s",url));
                }
            }
            else {
                Log.i(TAG,String.format("url=%s",url));
            }
        }
        else {
            Log.i(TAG,"url为空");
        }
    }
}
