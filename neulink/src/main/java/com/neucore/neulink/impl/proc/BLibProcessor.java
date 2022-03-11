package com.neucore.neulink.impl.proc;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.extend.ListenerFactory;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.extend.ActionResult;
import com.neucore.neulink.extend.ServiceFactory;
import com.neucore.neulink.extend.UpdateActionResult;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.IMessage;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.cmd.rrpc.BTLibSyncCmd;
import com.neucore.neulink.cmd.rrpc.BTLibSyncRes;
import com.neucore.neulink.cmd.rrpc.FaceCmd;
import com.neucore.neulink.cmd.rrpc.FaceData;
import com.neucore.neulink.cmd.rrpc.SyncInfo;
import com.neucore.neulink.cmd.rrpc.TLibPkgResult;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.FileUtils;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.NeuHttpHelper;
import com.neucore.neulink.util.RequestContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.ObjectUtil;

/**
 * 目标库处理器
 */
public class BLibProcessor extends GProcessor<BTLibSyncCmd, BTLibSyncRes, TLibPkgResult> {

    final private String ADD = "add",DEL = "del",UPDATE = "update",SYNC = "sync",PUSH = "push";
    final private String OBJ_TYPE_FACE = "face";
    private String libDir;

    public BLibProcessor(Context context){
        super(context);
        libDir = DeviceUtils.getTmpPath(context)+"/libDir";
    }

    public TLibPkgResult process(NeulinkTopicParser.Topic topic, BTLibSyncCmd cmd) {
        TLibPkgResult result = new TLibPkgResult();

        long pages = cmd.getPages();

        long offset = cmd.getOffset();

        Long lastOffset = null;

        if(ObjectUtil.isNotEmpty(msg)){
            Log.i(TAG,"pages="+pages+",offset="+offset+",MsgOffset="+lastOffset+",PkgStatus="+msg.getPkgStatus());

            lastOffset = msg.getOffset();

            if(IMessage.STATUS_FAIL.equalsIgnoreCase(msg.getPkgStatus())){
                offset = cmd.getOffset();
            }
            else if(IMessage.STATUS_SUCCESS.equalsIgnoreCase(msg.getStatus()) ){
                offset = lastOffset+1;
            }
        }

        result.setTotal(cmd.getTotal());
        result.setPages(pages);
        result.setOffset(offset);

        String rsl =null;
        long i=offset;
        String[] failed = null;
        for(;i<pages+1;i++){
            Log.d(TAG,"开始进入人脸offset:"+i+"下载");
            result.setOffset(i);
            try {

                failed = process(cmd.getReqtime(), cmd, i);
                if(i!=pages){//非最后请求包响应
                    result.setCode(STATUS_200);
                    result.setMsg(MESSAGE_SUCCESS);
                    BTLibSyncRes res = responseWrapper(cmd,result);
                    res.setFailed(failed);
                    rsl = JSonUtils.toString(res);
                    resLstRsl2Cloud(topic, rsl);
                }
                Log.d(TAG,"成功完成人脸offset:"+i+"下载");
                if(ObjectUtil.isNotEmpty(msg)){
                    updatePkg(msg.getId(),i, IMessage.STATUS_SUCCESS, MESSAGE_SUCCESS);
                }
            }
            catch (Throwable ex){
                Log.d(TAG,"人脸offset:"+i+"下载失败",ex);
                Log.e(TAG,"process",ex);
                result.setCode(STATUS_500);
                result.setMsg(ex.getMessage());
                if(ObjectUtil.isNotEmpty(msg)){
                    updatePkg(msg.getId(),i, IMessage.STATUS_FAIL, ex.getMessage());
                }
                return result;
            }
        }

        result.setFailed(failed);
        result.setCode(STATUS_200);
        result.setMsg(MESSAGE_SUCCESS);

        /**
         * 刷新内存
         */
        //UserService.getInstance(this.getContext()).onChanged();
        return result;

    }

    public BTLibSyncCmd parser(String payload){
        return (BTLibSyncCmd) JSonUtils.toObject(payload, BTLibSyncCmd.class);
    }

    public BTLibSyncRes responseWrapper(BTLibSyncCmd cmd, TLibPkgResult result) {
        BTLibSyncRes res = new BTLibSyncRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        res.setCode(result.getCode());
        res.setMsg(result.getMsg());
        res.setObjtype(cmd.getObjtype());
        res.setTotal(cmd.getTotal());
        res.setPages(cmd.getPages());
        res.setOffset(result.getOffset());
        res.setFailed(result.getFailed());
        return res;
    }

    public BTLibSyncRes fail(BTLibSyncCmd cmd, String message) {
        BTLibSyncRes res = new BTLibSyncRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        res.setCode(STATUS_500);
        res.setMsg(message);
        res.setObjtype(cmd.getObjtype());
        res.setTotal(cmd.getTotal());
        res.setPages(cmd.getPages());
        res.setOffset(cmd.getOffset());
        return res;
    }

    public BTLibSyncRes fail(BTLibSyncCmd cmd, int code, String message) {
        BTLibSyncRes res = new BTLibSyncRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        res.setCode(code);
        res.setMsg(message);
        res.setObjtype(cmd.getObjtype());
        res.setTotal(cmd.getTotal());
        res.setPages(cmd.getPages());
        res.setOffset(cmd.getOffset());
        return res;
    }

    protected String[] process(long reqTime, BTLibSyncCmd cmd, long offset)throws Exception {

        if(OBJ_TYPE_FACE.equalsIgnoreCase(cmd.getObjtype())){
            return faceSync(reqTime,cmd,offset);
        }
//        else if(OBJ_TYPE_LIC.equalsIgnoreCase(cmd.getObjtype())){
//            licSync(reqTime,cmd,msgId,offset);
//        }
        return null;
    }


    protected String[] faceSync(long reqTime, BTLibSyncCmd cmd, long offset) throws Exception{
        List failed = new ArrayList();
        //推送消息到达
        String jsonUrl = cmd.getDataUrl();
        int index = jsonUrl.lastIndexOf("/");
        String baseUrl = jsonUrl.substring(0,index+1);//包含'/'

        String newJsonFileUrl = baseUrl+offset+".json";

        String body = NeuHttpHelper.dldFile2String(newJsonFileUrl,3);

        SyncInfo syncInfo = (SyncInfo) JSonUtils.toObject(body, SyncInfo.class);

        String fileUrl = syncInfo.getFileUrl();
        //图片消息
        String reqdir = libDir+File.separator+RequestContext.getId();
        File toDir = new File(reqdir);
        if(toDir.exists()){
            FileUtils.deleteDirectory(toDir.getAbsolutePath());//清空
        }

        toDir.mkdirs();

        List<FaceData> params = null;
        try {
            File tmpFile = NeuHttpHelper.dld2File(getContext(), RequestContext.getId(),fileUrl,toDir);//下载zip文件

            FileUtils.unzipFile(tmpFile,toDir.getAbsolutePath());//解压zip文件

            tmpFile.delete();//删除zip文件
            String infoFileDir = reqdir+"/info";
            File info = new File(infoFileDir+"/"+offset+".json");
            params = facelibDataReader(info);//读取并解析facelib图片元数据

        }
        catch (IOException e){
            throw e;
        }
        catch (RuntimeException e){
            throw e;
        }
        Map images = null;
        ActionResult<Map<String,Object>> actionResult = null;
        if(ADD.equalsIgnoreCase(cmd.getCmdStr())||
                UPDATE.equalsIgnoreCase(cmd.getCmdStr())||
                SYNC.equalsIgnoreCase(cmd.getCmdStr())){
            /**
             * exit_id
             * image_type
             * face_sid
             */
            images = getImages(reqdir);//加载并计算图片的face特征值
            FaceCmd faceCmd = new FaceCmd();
            faceCmd.setReqtime(cmd.getReqtime());
            faceCmd.setOffset(offset);
            faceCmd.setPages(cmd.getPages());
            faceCmd.setCmd(cmd.getCmdStr());
            faceCmd.setPayload(params);
            faceCmd.setImageDatas(images);
            NeulinkEvent event = new NeulinkEvent(faceCmd);
            ICmdListener<UpdateActionResult<Map<String,Object>>,FaceCmd> listener = ListenerFactory.getInstance().getFaceListener();
            actionResult = listener.doAction(event);
            //libManagerService.insertOrUpdFacelib(reqTime,params,images);
        }
        else if(PUSH.equalsIgnoreCase(cmd.getCmdStr())){
            FaceCmd faceCmd = new FaceCmd();
            faceCmd.setReqtime(cmd.getReqtime());
            faceCmd.setCmd(cmd.getCmdStr());
            faceCmd.setOffset(offset);
            faceCmd.setPages(cmd.getPages());
            faceCmd.setPayload(params);
            NeulinkEvent event = new NeulinkEvent(faceCmd);
            ICmdListener<UpdateActionResult<Map<String,Object>>,FaceCmd> listener = ListenerFactory.getInstance().getFaceListener();
            actionResult = listener.doAction(event);
            //libManagerService.insertOrUpdFacelib(reqTime,params);
        }
        else if(DEL.equalsIgnoreCase(cmd.getCmdStr())){
            FaceCmd faceCmd = new FaceCmd();
            faceCmd.setReqtime(cmd.getReqtime());
            faceCmd.setCmd(cmd.getCmdStr());
            faceCmd.setOffset(offset);
            faceCmd.setPages(cmd.getPages());
            faceCmd.setPayload(params);
            NeulinkEvent event = new NeulinkEvent(faceCmd);
            ICmdListener<UpdateActionResult<Map<String,Object>>,FaceCmd> listener = ListenerFactory.getInstance().getFaceListener();
            actionResult = listener.doAction(event);
            //libManagerService.deleteFacelib(params);
        }
        if(actionResult !=null){
            Map<String,Object> temp = actionResult.getData();
            if(temp!=null){
                failed = (List)temp.remove("failed");
            }
        }


        Log.d(TAG,"message process successed");
        if(failed!=null && failed.size()>0){
            int size = failed.size();
            String[] faileds = new String[size];
            failed.toArray(faileds);
            return faileds;
        }
        /**
         * 清空图片临时目录
         */
        FileUtils.deleteDirectory(toDir.getAbsolutePath());//清空

        return null;
    }

    /**
     * [{"ext_id":"030","name":"\u6731\u62f1\u5e73","gender":0,"birthday":0}]
     * @param jsonDataFile
     */
    private static List<FaceData> facelibDataReader(File jsonDataFile) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new FileReader(jsonDataFile));
        StringBuffer sb = new StringBuffer();
        String line = null;
        while ((line=bufferedReader.readLine())!=null){
            sb.append(line);
        }
        bufferedReader.close();
        Type type =new TypeToken<List<FaceData>>() {}.getType();
        List<FaceData> params = JSonUtils.toList(sb.toString(), type);
        return params;
    }

    /**
     * 解析每张图片获得每张图片的ext_id,image_type,face_sid,face_sid_mask
     * 当图片特征无效时，进入到failed列表内
     * @return 返回多张图片信息
     * @throws IOException
     */
    private Map getImages(String reqdir)  {

        List<String> failed = new ArrayList<String>();
        /**
         * 当前请求的所有图片信息
         */
        Map imagesData = new HashMap();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        String imagesFileDir = reqdir + "/img";
        File[] images = new File(imagesFileDir).listFiles();//读取图片列表
        int len = images==null?0:images.length;
        for(int i=0;i<len;i++){

            File tmp = images[i];
            //工号.jpg即ext_id=工号
            String tmpName = tmp.getName();
            if(tmpName==null||tmpName.trim().length()==0){
                Log.i(TAG,"图片文件为空，其不符合【ext_id.jpg】规则");
                continue;
            }
            String ext_id = tmpName.substring(0, tmpName.indexOf("."));
            Map imageData = new HashMap();
            imageData.put("ext_id", ext_id);
            imageData.put("image_type", tmpName.substring(tmpName.indexOf(".") + 1));
            imageData.put("file",tmp);
            imagesData.put(ext_id,imageData);
        }

        return imagesData;
    }

    @Override
    /**
     * @deprecated
     */
    protected ICmdListener getListener() {
        throw new NeulinkException(STATUS_503,"该方法不能用。。。请直接调用ListenerFactory.getInstance().getFaceListener()");
    }
}
