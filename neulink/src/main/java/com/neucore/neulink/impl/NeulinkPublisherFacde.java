package com.neucore.neulink.impl;

import android.content.Context;
import android.util.Log;

import com.neucore.neulink.IProcessor;
import com.neucore.neulink.IResCallback;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.CmdRes;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.impl.registry.CallbackRegistry;
import com.neucore.neulink.impl.cmd.faceupld.v12.FaceUpload12;
import com.neucore.neulink.impl.cmd.lic.LicUpldCmd;
import com.neucore.neulink.impl.cmd.rmsg.UpgrRes;
import com.neucore.neulink.impl.cmd.tmptr.FaceTemp;
import com.neucore.neulink.impl.cmd.tmptr.FaceTempCmd;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.impl.NeulinkService;
import com.neucore.neulink.util.JSonUtils;

import java.util.Map;

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
        LicUpldCmd req = new LicUpldCmd();
        req.setNum(num);
        req.setColor(color);
        req.setImageUrl(imageUrl);
        req.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        req.setCmpCode(cmpCode);
        req.setLocationCode(locationCode);
        req.setPosition(position);

        String payload = JSonUtils.toString(req);
        String topic = "upld/req/carplateinfo";
        IResCallback resCallback = CallbackRegistry.getInstance().getResCallback("carplateinfo");
        service.publishMessage(topic, IProcessor.V1$0, payload, ConfigContext.getInstance().getConfig(ConfigContext.MQTT_QOS,1),resCallback);
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
        LicUpldCmd req = new LicUpldCmd();
        req.setNum(num);
        req.setColor(color);
        req.setImageUrl(imageUrl);
        req.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        req.setCmpCode(cmpCode);
        req.setLocationCode(locationCode);
        req.setPosition(position);

        String payload = JSonUtils.toString(req);
        String topic = "upld/req/carplateinfo";
        service.publishMessage(topic, IProcessor.V1$0, payload, ConfigContext.getInstance().getConfig(ConfigContext.MQTT_QOS,1),callback);
    }

    /**
     * 体温检测上报
     * upld/req/facetemprature/v1.0/${req_no}[/${md5}], qos=0
     */
    public void upldFacetmp(FaceTemp[] data){
        FaceTempCmd req = new FaceTempCmd();
        req.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        req.setData(data);

        String payload = JSonUtils.toString(req);
        String topic = "upld/req/facetemprature";
        IResCallback resCallback = CallbackRegistry.getInstance().getResCallback("facetemprature");
        service.publishMessage(topic, IProcessor.V1$0, payload, ConfigContext.getInstance().getConfig(ConfigContext.MQTT_QOS,1),resCallback);
    }

    /**
     * 体温检测上报
     * @param data
     * @param callback
     */
    public void upldFacetmp(FaceTemp[] data, IResCallback callback){
        FaceTempCmd req = new FaceTempCmd();
        req.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        req.setData(data);

        String payload = JSonUtils.toString(req);
        String topic = "upld/req/facetemprature";
        service.publishMessage(topic, IProcessor.V1$0, payload, ConfigContext.getInstance().getConfig(ConfigContext.MQTT_QOS,1),callback);
    }

    /**
     * 人脸抓拍上报
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
                    info.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());

                    if (info.getAiData()!=null){
                        info.getAiData().setDir(dir);
                    }

                    String payload = JSonUtils.toString(info);
                    String topic = "upld/req/faceinfo/"+info.getDeviceId();
                    IResCallback resCallback = CallbackRegistry.getInstance().getResCallback("faceinfo");
                    service.publishMessage(topic, IProcessor.V1$2, payload, ConfigContext.getInstance().getConfig(ConfigContext.MQTT_QOS,1),resCallback);
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

    /**
     * 人脸抓拍上报
     * @param url
     * @param info
     * @param callback
     */
    public void upldFaceInfo$1$2(String url, FaceUpload12 info, IResCallback callback){
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
                    service.publishMessage(topic, IProcessor.V1$2, payload, ConfigContext.getInstance().getConfig(ConfigContext.MQTT_QOS,1),callback);
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

    /**
     * 上报升级包下载进度
     * @param topicPrefix
     * @param reqId
     * @param progress
     */
    public void upldDownloadProgress(String topicPrefix,String version,String reqId,String progress){
        UpgrRes upgrRes = new UpgrRes();
        upgrRes.setCode(STATUS_200);
        upgrRes.setMsg("下载中");
        upgrRes.setProgress(progress);
        upgrRes.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        String payload = JSonUtils.toString(upgrRes);
        service.publishMessage(topicPrefix,version,reqId,payload,ConfigContext.getInstance().getConfig(ConfigContext.MQTT_QOS,1));
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
        String topicPrefix = String.format("rmsg/res/%s",biz);
        CmdRes res = new CmdRes();
        res.setCode(code);
        res.setMsg(message);
        res.setCmdStr(mode);
        res.setData(payload);
        IResCallback resCallback = CallbackRegistry.getInstance().getResCallback(biz.toLowerCase());
        response(topicPrefix,version,reqId,res,resCallback);
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
    public void rmsgResponse(String biz, String version, String reqId, String mode, Integer code, String message, String payload, IResCallback callback){
        String topicPrefix = String.format("rmsg/res/%s",biz);
        CmdRes res = new CmdRes();
        res.setCode(code);
        res.setMsg(message);
        res.setCmdStr(mode);
        res.setData(payload);
        response(topicPrefix,version,reqId,res,callback);
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
        String topicPrefix = String.format("upld/req/%s/%s",biz, ServiceRegistry.getInstance().getDeviceService().getExtSN());
        CmdRes res = new CmdRes();
        res.setCode(code);
        res.setMsg(message);
        res.setCmdStr(mode);
        res.setData(payload);
        IResCallback resCallback = CallbackRegistry.getInstance().getResCallback(biz.toLowerCase());
        response(topicPrefix,version,reqId,res,resCallback);
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
    public void upldRequest(String biz, String version, String reqId, String mode, Integer code, String message, Object payload, IResCallback callback){
        String topicPrefix = String.format("upld/req/%s/%s",biz, ServiceRegistry.getInstance().getDeviceService().getExtSN());
        CmdRes res = new CmdRes();
        res.setCode(code);
        res.setMsg(message);
        res.setCmdStr(mode);
        res.setData(payload);
        response(topicPrefix,version,reqId,res,callback);
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
        String topicPrefix = String.format("rrpc/res/%s",biz);
        CmdRes res = new CmdRes();
        res.setCode(code);
        res.setMsg(message);
        res.setCmdStr(mode);
        res.setData(payload);
        IResCallback resCallback = CallbackRegistry.getInstance().getResCallback(biz.toLowerCase());
        response(topicPrefix,version,reqId,res,resCallback);
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
        String topicPrefix = String.format("rrpc/res/%s",biz);
        CmdRes res = new CmdRes();
        res.setCode(code);
        res.setMsg(message);
        res.setCmdStr(mode);
        res.setData(payload);
        response(topicPrefix,version,reqId,res,callback);
    }

    void response(String topicPrefix, String biz, String version, String reqId, String mode, Integer code, String message, Map<String,String> heads){
        CmdRes res = new CmdRes();
        res.setHeaders(heads);
        res.setCode(code);
        res.setMsg(message);
        res.setCmdStr(mode);

        IResCallback resCallback = CallbackRegistry.getInstance().getResCallback(biz.toLowerCase());
        response(topicPrefix,version,reqId,res,resCallback);
    }
    private void response(String topicPrefix, String version,String reqId, CmdRes res, IResCallback callback){
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        String payloadStr = JSonUtils.toString(res);
        service.publishMessage(topicPrefix,version,reqId,payloadStr,ConfigContext.getInstance().getConfig(ConfigContext.MQTT_QOS,1),callback);
    }
}
