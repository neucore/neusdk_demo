package com.neucore.neulink.impl;

import android.content.Context;

import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.IProcessor;
import com.neucore.neulink.IResCallback;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.impl.registry.CallbackRegistry;
import com.neucore.neulink.impl.cmd.faceupld.v12.FaceUpload12;
import com.neucore.neulink.impl.cmd.lic.LicUpldCmd;
import com.neucore.neulink.impl.cmd.rmsg.UpgrRes;
import com.neucore.neulink.impl.cmd.tmptr.FaceTemp;
import com.neucore.neulink.impl.cmd.tmptr.FaceTempCmd;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.util.JSonUtils;

import java.util.Map;

import cn.hutool.core.lang.UUID;
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
     * 车牌抓拍上报
     * upld/req/carplateinfo/v1.0/${req_no}[/${md5}], qos=0
     */
    public void upldLic(String num,String color,String imageUrl,String cmpCode,String locationCode,String position){
        IResCallback callback = CallbackRegistry.getInstance().getResCallback(NeulinkConst.NEULINK_BIZ_CAR_PLATE_INFO);
        upldLic(num,color,imageUrl,cmpCode,locationCode,position,callback);
    }

    /**
     * 车牌抓拍上报
     * @param num
     * @param color
     * @param imageUrl
     * @param cmpCode
     * @param locationCode
     * @param position
     * @param callback
     */
    public void upldLic(String num, String color, String imageUrl, String cmpCode, String locationCode, String position, IResCallback callback){
        upldLic(num,color,imageUrl,cmpCode,locationCode,position,ConfigContext.getInstance().getConfig(ConfigContext.MQTT_QOS,1),callback);
    }

    /**
     *
     * @param num
     * @param color
     * @param imageUrl
     * @param cmpCode
     * @param locationCode
     * @param position
     * @param qos
     * @param callback
     */
    public void upldLic(String num, String color, String imageUrl, String cmpCode, String locationCode, String position,int qos, IResCallback callback){
        upldLic(num,color,imageUrl,cmpCode,locationCode,position,qos,ConfigContext.getInstance().getConfig(ConfigContext.MQTT_RETAINED,false),callback);
    }

    /**
     *
     * @param num
     * @param color
     * @param imageUrl
     * @param cmpCode
     * @param locationCode
     * @param position
     * @param qos
     * @param retained
     * @param callback
     */
    public void upldLic(String num, String color, String imageUrl, String cmpCode, String locationCode, String position,int qos,boolean retained,IResCallback callback) {
        LicUpldCmd req = new LicUpldCmd();
        req.setNum(num);
        req.setColor(color);
        req.setImageUrl(imageUrl);
        req.setCmpCode(cmpCode);
        req.setLocationCode(locationCode);
        req.setPosition(position);
        req.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());

        String payload = JSonUtils.toString(req);
        String topic = "upld/req/carplateinfo";
        service.publishMessage(topic, IProcessor.V1$0, payload, qos,retained,callback);
    }
    /**
     * 体温检测上报
     * upld/req/facetemprature/v1.0/${req_no}[/${md5}], qos=0
     */
    public void upldFacetmp(FaceTemp[] data){
        upldFacetmp(data,ConfigContext.getInstance().getConfig(ConfigContext.MQTT_QOS,1));
    }

    /**
     *
     * @param data
     * @param qos
     */
    public void upldFacetmp(FaceTemp[] data,int qos){
        upldFacetmp(data,qos,ConfigContext.getInstance().getConfig(ConfigContext.MQTT_RETAINED,false));
    }

    /**
     *
     * @param data
     * @param qos
     * @param retained
     */
    public void upldFacetmp(FaceTemp[] data,int qos,boolean retained){
        IResCallback resCallback = CallbackRegistry.getInstance().getResCallback(NeulinkConst.NEULINK_BIZ_FACE_TEMPERATURE);
        upldFacetmp(data,qos,retained,resCallback);
    }

    /**
     * 体温检测上报
     * @param data
     * @param callback
     */
    public void upldFacetmp(FaceTemp[] data, IResCallback callback){
        upldFacetmp(data,ConfigContext.getInstance().getConfig(ConfigContext.MQTT_QOS,1),callback);
    }

    /**
     *
     * @param data
     * @param qos
     * @param callback
     */
    public void upldFacetmp(FaceTemp[] data,int qos,IResCallback callback){
        upldFacetmp(data,qos,ConfigContext.getInstance().getConfig(ConfigContext.MQTT_RETAINED,false),callback);
    }

    /**
     *
     * @param data
     * @param qos
     * @param retained
     * @param callback
     */
    public void upldFacetmp(FaceTemp[] data,int qos,boolean retained, IResCallback callback){
        FaceTempCmd req = new FaceTempCmd();
        req.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        req.setData(data);
        String payload = JSonUtils.toString(req);
        String topic = "upld/req/facetemprature";
        service.publishMessage(topic, IProcessor.V1$0,UUID.fastUUID().toString(), payload, qos,retained,callback);
    }
    /**
     * 人脸抓拍上报
     * 1.2版本协议
     * @param url 人脸照片的url
     * @param info 人脸识别信息
     */
    public void upldFaceInfo$1$2(String url, FaceUpload12 info){
        upldFaceInfo$1$2(url,info,CallbackRegistry.getInstance().getResCallback(NeulinkConst.NEULINK_BIZ_FACE_INFO));
    }

    /**
     * 人脸抓拍上报
     * @param url
     * @param info
     * @param callback
     */
    public void upldFaceInfo$1$2(String url, FaceUpload12 info, IResCallback callback){
        upldFaceInfo$1$2(url,info,ConfigContext.getInstance().getConfig(ConfigContext.MQTT_QOS,1),callback);
    }

    /**
     *
     * @param url
     * @param info
     * @param qos
     * @param callback
     */
    public void upldFaceInfo$1$2(String url, FaceUpload12 info,int qos, IResCallback callback){
        upldFaceInfo$1$2(url,info,qos,ConfigContext.getInstance().getConfig(ConfigContext.MQTT_RETAINED,false),callback);
    }

    /**
     *
     * @param url
     * @param info
     * @param qos
     * @param retained
     * @param callback
     */
    public void upldFaceInfo$1$2(String url, FaceUpload12 info, int qos,boolean retained,IResCallback callback){
        if(!ObjectUtil.isEmpty(url)){
            int index = url.lastIndexOf("/");
            if(index!=-1){
                String dir =url.substring(0,index);
                if(!ObjectUtil.isEmpty(dir)){
                    info.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());

                    if (info.getAiData()!=null){
                        info.getAiData().setDir(dir);
                    }
                    String payload = JSonUtils.toString(info);
                    String topic = "upld/req/faceinfo/"+info.getDeviceId();
                    service.publishMessage(topic, IProcessor.V1$2, UUID.fastUUID().toString(), payload, qos,retained,callback);
                }
                else{
                    NeuLogUtils.iTag(TAG,String.format("url=%s",url));
                }
            }
            else {
                NeuLogUtils.iTag(TAG,String.format("url=%s",url));
            }
        }
        else {
            NeuLogUtils.iTag(TAG,"url为空");
        }
    }

    /**
     * 上报升级包下载进度
     * @param topicPrefix
     * @param reqId
     * @param progress
     */
    public void upldDownloadProgress(String topicPrefix,String version,String reqId,String progress){
        upldDownloadProgress(topicPrefix,version,reqId,progress,ConfigContext.getInstance().getConfig(ConfigContext.MQTT_QOS,1));
    }

    /**
     *
     * @param topicPrefix
     * @param version
     * @param reqId
     * @param progress
     * @param qos
     */
    public void upldDownloadProgress(String topicPrefix,String version,String reqId,String progress,int qos){
        upldDownloadProgress(topicPrefix,version,reqId,progress,qos,ConfigContext.getInstance().getConfig(ConfigContext.MQTT_RETAINED,false));
    }

    /**
     *
     * @param topicPrefix
     * @param version
     * @param reqId
     * @param progress
     * @param qos
     * @param retained
     */
    public void upldDownloadProgress(String topicPrefix,String version,String reqId,String progress,int qos,boolean retained){
        UpgrRes upgrRes = new UpgrRes();
        upgrRes.setCode(STATUS_200);
        upgrRes.setMsg("下载中");
        upgrRes.setProgress(progress);
        upgrRes.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        response(false,topicPrefix,version,reqId,upgrRes,qos,retained,null);
    }
    /**
     * msg/req/devinfo/v1.0/${req_no}[/${md5}][/dev_id]
     * msg/req/status/v1.0/${req_no}[/${md5}][/${custid}][/${storeid}][/zoneid][/dev_id]
     * msg/req/stat/v1.0/${req_no}[/${md5}][/${custid}][/${storeid}][/zoneid][/dev_id]
     * msg/req/connect/v1.0/${req_no}[/${md5}][/${custid}][/${storeid}][/zoneid][/dev_id]
     * msg/req/disconnect/v1.0/${req_no}[/${md5}][/${custid}][/${storeid}][/zoneid][/dev_id]
     * msg/req/lwt/v1.0/${custid}/${dev_id}
     */

    /**
     * rmsg请求异步处理响应
     * rmsg/res/${biz}/${version}
     * @param biz
     * @param version
     * @param reqId
     * @param mode
     * @param code
     * @param message
     * @param payload
     */
    public void rmsgResponse(String biz,String version,String reqId,String mode,Integer code,String message,String payload){
        IResCallback resCallback = CallbackRegistry.getInstance().getResCallback(biz.toLowerCase());
        rmsgResponse(biz,version,reqId,mode,code,message,payload,resCallback);
    }

    /**
     *
     * @param biz
     * @param version
     * @param reqId
     * @param mode
     * @param code
     * @param message
     * @param payload
     * @param callback
     */
    public void rmsgResponse(String biz, String version, String reqId, String mode, Integer code, String message, String payload,IResCallback callback){
        rmsgResponse(biz,version,reqId,mode,code,message,payload,ConfigContext.getInstance().getConfig(ConfigContext.MQTT_QOS,1),callback);
    }
    /**
     * rmsg请求异步处理响应
     * @param biz
     * @param version
     * @param reqId
     * @param mode
     * @param code
     * @param message
     * @param payload
     * @param callback
     */
    public void rmsgResponse(String biz, String version, String reqId, String mode, Integer code, String message, String payload,int qos, IResCallback callback){
        rmsgResponse(biz,version,reqId,mode,code,message,payload,qos,ConfigContext.getInstance().getConfig(ConfigContext.MQTT_RETAINED,false),callback);
    }

    /**
     *
     * @param biz
     * @param version
     * @param reqId
     * @param mode
     * @param code
     * @param message
     * @param payload
     * @param qos
     * @param retained
     * @param callback
     */
    public void rmsgResponse(String biz, String version, String reqId, String mode, Integer code, String message, String payload,int qos,boolean retained, IResCallback callback){
        String topicPrefix = String.format("rmsg/res/%s",biz);
        CmdRes res = new CmdRes();
        res.setCode(code);
        res.setMsg(message);
        res.setCmdStr(mode);
        res.setData(payload);
        response(topicPrefix,version,reqId,res,qos,retained,callback);
    }

    /**
     * 设备发起的主动请求【人脸抓拍、体温检测、车牌抓拍】
     * @param biz
     * @param version
     * @param reqId
     * @param mode
     * @param code
     * @param message
     * @param payload
     */
    public void upldRequest(String biz,String version,String reqId,String mode,Integer code,String message,Object payload){
        IResCallback callback = CallbackRegistry.getInstance().getResCallback(biz.toLowerCase());
        upldRequest(biz,version,reqId,mode,code,message,payload,callback);
    }

    /**
     * 设备发起的主动请求【人脸抓拍、体温检测、车牌抓拍】
     * @param biz
     * @param version
     * @param reqId
     * @param mode
     * @param code
     * @param message
     * @param payload
     * @param callback
     */
    public void upldRequest(String biz, String version, String reqId, String mode, Integer code, String message, Object payload,IResCallback callback){
        upldRequest(biz,version,reqId,mode,code,message,payload,ConfigContext.getInstance().getConfig(ConfigContext.MQTT_QOS,1),callback);
    }

    /**
     *
     * @param biz
     * @param version
     * @param reqId
     * @param mode
     * @param code
     * @param message
     * @param payload
     * @param qos
     * @param callback
     */
    public void upldRequest(String biz, String version, String reqId, String mode, Integer code, String message, Object payload,int qos,IResCallback callback){
        upldRequest(biz,version,reqId,mode,code,message,payload,qos,ConfigContext.getInstance().getConfig(ConfigContext.MQTT_RETAINED,false),callback);
    }

    /**
     *
     * @param biz
     * @param version
     * @param reqId
     * @param mode
     * @param code
     * @param message
     * @param payload
     * @param qos
     * @param retained
     * @param callback
     */
    public void upldRequest(String biz, String version, String reqId, String mode, Integer code, String message, Object payload,int qos,boolean retained,IResCallback callback){
        String topicPrefix = String.format("upld/req/%s/%s",biz, ServiceRegistry.getInstance().getDeviceService().getExtSN());
        CmdRes res = new CmdRes();
        res.setCode(code);
        res.setMsg(message);
        res.setCmdStr(mode);
        res.setData(payload);
        response(topicPrefix,version,reqId,res,qos,retained,callback);
    }
    /**
     * rrpc请求异步处理响应
     * rrpc/res/${biz}/${version}
     * @param biz
     * @param version
     * @param reqId
     * @param mode
     * @param code
     * @param message
     * @param payload
     */
    public void rrpcResponse(String biz,String version,String reqId,String mode,Integer code,String message,String payload){
        IResCallback resCallback = CallbackRegistry.getInstance().getResCallback(biz.toLowerCase());
        rrpcResponse(biz,version,reqId,mode,code,message,payload,resCallback);
    }

    /**
     * rrpc请求异步处理响应
     * @param biz
     * @param version
     * @param reqId
     * @param mode
     * @param code
     * @param message
     * @param payload
     * @param callback
     */
    public void rrpcResponse(String biz, String version, String reqId, String mode, Integer code, String message, String payload, IResCallback callback){
        rrpcResponse(biz,version,reqId,mode,code,message,payload,ConfigContext.getInstance().getConfig(ConfigContext.MQTT_QOS,1),callback);
    }

    /**
     *
     * @param biz
     * @param version
     * @param reqId
     * @param mode
     * @param code
     * @param message
     * @param payload
     * @param qos
     * @param callback
     */
    public void rrpcResponse(String biz, String version, String reqId, String mode, Integer code, String message, String payload,int qos, IResCallback callback){
        rrpcResponse(biz,version,reqId,mode,code,message,payload,qos,ConfigContext.getInstance().getConfig(ConfigContext.MQTT_RETAINED,false),callback);
    }

    /**
     *
     * @param biz
     * @param version
     * @param reqId
     * @param mode
     * @param code
     * @param message
     * @param payload
     * @param qos
     * @param retained
     * @param callback
     */
    public void rrpcResponse(String biz, String version, String reqId, String mode, Integer code, String message, String payload,int qos,boolean retained, IResCallback callback){
        String topicPrefix = String.format("rrpc/res/%s",biz);
        CmdRes res = new CmdRes();
        res.setCode(code);
        res.setMsg(message);
        res.setCmdStr(mode);
        res.setData(payload);
        response(topicPrefix,version,reqId,res,qos,retained,callback);
    }

    /**
     *
     * @param topicPrefix
     * @param version
     * @param reqId
     * @param res
     * @param qos
     * @param retained
     * @param callback
     */
    private void response(String topicPrefix, String version,String reqId, CmdRes res, int qos,boolean retained,IResCallback callback){
        response(false,topicPrefix,version,reqId,res,qos,retained,callback);
    }
    /**
     *
     * @param debug
     * @param topicPrefix
     * @param biz
     * @param version
     * @param reqId
     * @param mode
     * @param code
     * @param message
     * @param heads
     */
    void response(boolean debug,String topicPrefix, String biz, String version, String reqId, String mode, Integer code, String message, Map<String,String> heads){
        CmdRes res = new CmdRes();
        res.setHeaders(heads);
        res.setCode(code);
        res.setMsg(message);
        res.setCmdStr(mode);
        IResCallback resCallback = CallbackRegistry.getInstance().getResCallback(biz.toLowerCase());
        response(debug,topicPrefix,version,reqId,res,ConfigContext.getInstance().getConfig(ConfigContext.MQTT_QOS,1),ConfigContext.getInstance().getConfig(ConfigContext.MQTT_RETAINED,false), resCallback);
    }

    /**
     *
     * @param debug
     * @param topicPrefix
     * @param version
     * @param reqId
     * @param res
     * @param qos
     * @param retained
     * @param callback
     */
    private void response(boolean debug,String topicPrefix, String version,String reqId, CmdRes res, int qos,boolean retained,IResCallback callback){
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        String payloadStr = JSonUtils.toString(res);
        service.publishMessage(debug,topicPrefix,version,reqId,payloadStr,qos,retained,callback);
    }
}
