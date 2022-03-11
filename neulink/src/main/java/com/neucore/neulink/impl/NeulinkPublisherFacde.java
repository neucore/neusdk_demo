package com.neucore.neulink.impl;

import android.content.Context;
import android.util.Log;

import com.neucore.neulink.IProcessor;
import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.cmd.faceupld.v12.FaceUpload12;
import com.neucore.neulink.cmd.lic.LicUpldCmd;
import com.neucore.neulink.cmd.rmsg.UpgrRes;
import com.neucore.neulink.cmd.tmptr.FaceTemp;
import com.neucore.neulink.cmd.tmptr.FaceTempCmd;
import com.neucore.neulink.extend.ServiceFactory;
import com.neucore.neulink.util.JSonUtils;

import cn.hutool.core.util.ObjectUtil;

/**
 * sdk应用信息上报接口：主要包括车牌上报、体温上报、人脸信息上报
 */
public class NeulinkPublisherFacde implements NeulinkConst{

    private String TAG = TAG_PREFIX+"PublisherFacde";

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
        req.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
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
        req.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        req.setData(data);

        String payload = JSonUtils.toString(req);
        String topic = "upld/req/facetemprature";
        service.publishMessage(topic, IProcessor.V1$0, payload, 0);
    }

    /**
     * 上报升级包下载进度
     * @param topicPrefix
     * @param reqId
     * @param progress
     */
    public void upldDownloadProgress(String topicPrefix,String reqId,String progress){
        UpgrRes upgrRes = new UpgrRes();
        upgrRes.setCode(STATUS_200);
        upgrRes.setMsg("下载中");
        upgrRes.setProgress(progress);
        upgrRes.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        String payload = JSonUtils.toString(upgrRes);
        service.publishMessage(topicPrefix,IProcessor.V1$0,reqId,payload,0);
    }
    /**
     * rmsg请求响应
     * rmsg/res/${biz}/${version}
     * @param biz
     * @param version
     * @param reqId
     * @param mode
     * @param code
     * @param message
     * @param payload
     */
    public void rmsgResponse(String biz,String version,String reqId,String mode,Integer code,String message,ObjectUtil payload){
        String topicPrefix = String.format("rmsg/res/%s/%s",biz,version);
        UpgrRes upgrRes = new UpgrRes();
        upgrRes.setCode(code);
        upgrRes.setMsg(message);
        upgrRes.setCmdStr(mode);
        upgrRes.setData(payload);
        upldResponse(topicPrefix,reqId,upgrRes);
    }
    /**
     * rrpc请求响应
     * rrpc/res/${biz}/${version}
     * @param biz
     * @param version
     * @param reqId
     * @param mode
     * @param code
     * @param message
     * @param payload
     */
    public void rrpcResponse(String biz,String version,String reqId,String mode,Integer code,String message,ObjectUtil payload){
        String topicPrefix = String.format("rrpc/res/%s/%s/%s",biz,ServiceFactory.getInstance().getDeviceService().getExtSN(),version);
        UpgrRes upgrRes = new UpgrRes();
        upgrRes.setCode(code);
        upgrRes.setMsg(message);
        upgrRes.setCmdStr(mode);
        upgrRes.setData(payload);
        upldResponse(topicPrefix,reqId,upgrRes);
    }
    /**
     * 抓拍上传
     * @param biz
     * @param version
     * @param reqId
     * @param mode
     * @param code
     * @param message
     * @param payload
     */
    public void upldRequest(String biz,String version,String reqId,String mode,Integer code,String message,Object payload){
        String topicPrefix = String.format("upld/req/%s/%s/%s",biz,ServiceFactory.getInstance().getDeviceService().getExtSN(),version);
        UpgrRes upgrRes = new UpgrRes();
        upgrRes.setCode(code);
        upgrRes.setMsg(message);
        upgrRes.setCmdStr(mode);
        upgrRes.setData(payload);
        upldResponse(topicPrefix,reqId,upgrRes);
    }

    private void upldResponse(String topicPrefix,String reqId,UpgrRes upgrRes){

        String[] lspTopics = topicPrefix.split("/v\\d+\\.\\d+(\\.\\d+)?(/)?");
        String[] uspTopics = topicPrefix.split("/V\\d+\\.\\d+(\\.\\d+)?(/)?");

        if(lspTopics.length==topicPrefix.length() && uspTopics.length==topicPrefix.length()){
            throw new NeulinkException(STATUS_505,"topic不符合规范:没有版本字段信息");
        }
        if(lspTopics.length!=topicPrefix.length()){
            topicPrefix = lspTopics[0];
        }
        if(uspTopics.length!=topicPrefix.length()){
            topicPrefix = uspTopics[0];
        }
        String[] topicArray = topicPrefix.split("/");
        if("req".equalsIgnoreCase(topicArray[1]) && "res".equalsIgnoreCase(topicArray[1])){
            throw new NeulinkException(STATUS_505,"topic不符合规范");
        }
        String payloadStr = JSonUtils.toString(upgrRes);
        upgrRes.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        service.publishMessage(topicPrefix,IProcessor.V1$0,reqId,payloadStr,0);
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
                    info.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());

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
