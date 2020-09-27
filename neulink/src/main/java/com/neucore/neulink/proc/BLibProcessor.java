package com.neucore.neulink.proc;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.neucore.neulink.extend.ListenerFactory;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.extend.UpdateResult;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.rrpc.BTLibSync;
import com.neucore.neulink.rrpc.BTLibSyncRes;
import com.neucore.neulink.rrpc.FaceCmd;
import com.neucore.neulink.rrpc.FaceData;
import com.neucore.neulink.rrpc.SyncInfo;
import com.neucore.neulink.rrpc.TLibPkgResult;
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

/**
 * 目标库处理器
 */
public class BLibProcessor extends GProcessor<BTLibSync, BTLibSyncRes, TLibPkgResult> {

    final private String ADD = "add",DEL = "del",UPDATE = "update",SYNC = "sync",PUSH = "push";
    final private String OBJ_TYPE_FACE = "face";
    private String libDir;

    private Object lock = new Object();

    public BLibProcessor(Context context){
        super(context);
        libDir = DeviceUtils.getTmpPath(context)+"/libDir";
    }

    public TLibPkgResult process(NeulinkTopicParser.Topic topic, BTLibSync cmd) {
        TLibPkgResult result = new TLibPkgResult();

        long pages = cmd.getPages();

        long offset = cmd.getOffset();

//        long lastOffset = msg.getOffset();

        //Log.i(TAG,"pages="+pages+",offset="+offset+",MsgOffset="+lastOffset+",PkgStatus="+msg.getPkgStatus());

//        if(Message.STATUS_FAIL.equalsIgnoreCase(msg.getPkgStatus())){
//            offset = msg.getOffset();
//        }
//        else if(Message.STATUS_SUCCESS.equalsIgnoreCase(msg.getStatus()) ){
//            offset = lastOffset+1;
//        }

        result.setTotal(cmd.getTotal());
        result.setPages(pages);
        result.setOffset(offset);

        String rsl =null;
        long i=offset;
        String[] failed = null;
        for(;i<pages+1;i++){
            result.setOffset(i);
            try {

                failed = process(cmd.getReqtime(), cmd, i);
                if(i!=pages){//非最后请求包响应
                    result.setCode(200);
                    result.setMsg("success");
                    BTLibSyncRes res = responseWrapper(cmd,result);
                    res.setFailed(failed);
                    rsl = JSonUtils.toString(res);
                    resLstRsl2Cloud(topic, rsl);
                }
                //updatePkg(msg.getId(),i, Message.STATUS_SUCCESS, "success");
            }
            catch (Throwable ex){
                Log.e(TAG,"process",ex);
                result.setCode(500);
                result.setMsg(ex.getMessage());
                //updatePkg(msg.getId(),i, Message.STATUS_FAIL, ex.getMessage());
                return result;
            }
        }

        result.setFailed(failed);
        result.setCode(200);
        result.setMsg("success");

        /**
         * 刷新内存
         */
        //UserService.getInstance(this.getContext()).onChanged();
        return result;

    }

    public BTLibSync parser(String payload){
        return (BTLibSync) JSonUtils.toObject(payload, BTLibSync.class);
    }

    public BTLibSyncRes responseWrapper(BTLibSync cmd,TLibPkgResult result) {
        BTLibSyncRes res = new BTLibSyncRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setDeviceId(DeviceUtils.getDeviceId(this.getContext()));
        res.setCode(result.getCode());
        res.setMsg(result.getMsg());
        res.setObjtype(cmd.getObjtype());
        res.setTotal(cmd.getTotal());
        res.setPages(cmd.getPages());
        res.setOffset(result.getOffset());
        res.setFailed(result.getFailed());
        return res;
    }

    public BTLibSyncRes fail(BTLibSync cmd,String message) {
        BTLibSyncRes res = new BTLibSyncRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setDeviceId(DeviceUtils.getDeviceId(this.getContext()));
        res.setCode(500);
        res.setMsg(message);
        res.setObjtype(cmd.getObjtype());
        res.setTotal(cmd.getTotal());
        res.setPages(cmd.getPages());
        res.setOffset(cmd.getOffset());
        return res;
    }

    public BTLibSyncRes fail(BTLibSync cmd,int code,String message) {
        BTLibSyncRes res = new BTLibSyncRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setDeviceId(DeviceUtils.getDeviceId(this.getContext()));
        res.setCode(code);
        res.setMsg(message);
        res.setObjtype(cmd.getObjtype());
        res.setTotal(cmd.getTotal());
        res.setPages(cmd.getPages());
        res.setOffset(cmd.getOffset());
        return res;
    }

    protected String[] process(long reqTime,BTLibSync cmd,long offset)throws Exception {

        if(OBJ_TYPE_FACE.equalsIgnoreCase(cmd.getObjtype())){
            return faceSync(reqTime,cmd,offset);
        }
//        else if(OBJ_TYPE_LIC.equalsIgnoreCase(cmd.getObjtype())){
//            licSync(reqTime,cmd,msgId,offset);
//        }
        return null;
    }

//    protected void licSync(long reqTime,BTLibSync cmd,long msgId,long offset) throws Exception{
//        //推送消息到达
//        String jsonUrl = cmd.getDataUrl();
//        int index = jsonUrl.lastIndexOf("/");
//        String baseUrl = jsonUrl.substring(0,index+1);//包含'/'
//
//        String newJsonFileUrl = baseUrl+offset+".json";
//
//        String body = NeuHttpHelper.dldFile2String(newJsonFileUrl,3);
//
//        SyncInfo syncInfo = (SyncInfo) JSonUtils.toObject(body, SyncInfo.class);
//
//        String fileUrl = syncInfo.getFileUrl();
//        String md5 = syncInfo.getMd5();
//        String reqdir = libDir+File.separator+RequestContext.getId();
//        File toDir = new File(reqdir);
//        if(toDir.exists()){
//            FileUtils.deleteDirectory(toDir.getAbsolutePath());//清空
//        }
//
//        toDir.mkdirs();
//
//        List<FaceData> params = null;
//        try {
//            File tmpFile = NeuHttpHelper.dld2File(getContext(), RequestContext.getId(), fileUrl, toDir);//下载zip文件
//
//            FileUtils.unzipFile(tmpFile, toDir.getAbsolutePath());//解压zip文件
//
//            tmpFile.delete();//删除zip文件
//            String infoFileDir = reqdir + "/info";
//            File info = new File(infoFileDir + "/" + offset + ".json");
//            List<String> lics = liclibDataReader(info);
//            if (ADD.equalsIgnoreCase(cmd.getCmdStr()) ||
//                    UPDATE.equalsIgnoreCase(cmd.getCmdStr()) ||
//                    SYNC.equalsIgnoreCase(cmd.getCmdStr()) ||
//                    PUSH.equalsIgnoreCase(cmd.getCmdStr())){
//                libManagerService.insertOrUpdLicNumlib(reqTime, lics);
//            }
//            else if(DEL.equalsIgnoreCase(cmd.getCmdStr())){
//                libManagerService.deleteLiclib(lics);
//            }
//        }
//        catch (IOException e){
//            throw e;
//        }
//        catch (RuntimeException e){
//            throw e;
//        }
//    }

//    private static List<String> liclibDataReader(File jsonDataFile) throws IOException {
//
//        BufferedReader bufferedReader = new BufferedReader(new FileReader(jsonDataFile));
//        StringBuffer sb = new StringBuffer();
//        String line = null;
//        while ((line=bufferedReader.readLine())!=null){
//            sb.append(line);
//        }
//        bufferedReader.close();
//        Type type =new TypeToken<List<String>>() {}.getType();
//        List<String> params = JSonUtils.toList(sb.toString(), type);
//        return params;
//    }

    protected String[] faceSync(long reqTime,BTLibSync cmd,long offset) throws Exception{
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
        UpdateResult result = null;
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

            result = ListenerFactory.getInstance().getFaceListener().doAction(event);

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
            result = ListenerFactory.getInstance().getFaceListener().doAction(event);
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
            result = ListenerFactory.getInstance().getFaceListener().doAction(event);
            //libManagerService.deleteFacelib(params);
        }
        if(result!=null){
            Map<String,Object> temp = result.getDatas();
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

    public static void main(String[] args) throws Exception{
        List<FaceData> data = facelibDataReader(new File("/Users/alex.zhu/Downloads/test.json"));
        System.out.println(data.size());
    }
}
