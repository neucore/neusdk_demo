package com.neucore.neulink.impl.proc;

import android.content.Context;

import com.neucore.neulink.log.NeuLogUtils;
import com.google.gson.reflect.TypeToken;
import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.IBlib$ObjtypeProcessor;
import com.neucore.neulink.impl.cmd.rrpc.FaceCmdRes;
import com.neucore.neulink.impl.cmd.rrpc.FaceCmd;
import com.neucore.neulink.impl.cmd.rrpc.FaceData;
import com.neucore.neulink.impl.cmd.rrpc.FacePkgActionResult;
import com.neucore.neulink.impl.cmd.rrpc.SyncInfo;
import com.neucore.neulink.impl.ActionResult;
import com.neucore.neulink.impl.registry.ServiceRegistry;
import com.neucore.neulink.util.ContextHolder;
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
public final class DefaultFaceSyncProcessor implements IBlib$ObjtypeProcessor<FaceCmd, FaceCmdRes, FacePkgActionResult> {

    private String libDir;
    private Context context;
    protected String TAG = TAG_PREFIX+this.getClass().getSimpleName();
    public DefaultFaceSyncProcessor(){
        Context context = ContextHolder.getInstance().getContext();
        libDir = DeviceUtils.getTmpPath(context)+"/faceDir";
    }
    public DefaultFaceSyncProcessor(Context context){
        this.context = context;
        libDir = DeviceUtils.getTmpPath(context)+"/libDir";
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getBiz(){
        return NEULINK_BIZ_BLIB;
    }

    public String getObjType(){
        return NEULINK_BIZ_BLIB_FACE;
    }

    public FaceCmdRes responseWrapper(FaceCmd cmd, FacePkgActionResult result) {
        FaceCmdRes res = new FaceCmdRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(result.getCode());
        res.setMsg(result.getMessage());
        res.setObjtype(cmd.getObjtype());
        res.setTotal(cmd.getTotal());
        res.setPages(cmd.getPages());
        res.setOffset(result.getOffset());
        res.setFailed(result.getData());
        return res;
    }

    public FaceCmdRes fail(FaceCmd cmd, String message) {
        FaceCmdRes res = new FaceCmdRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(STATUS_500);
        res.setMsg(message);
        res.setObjtype(cmd.getObjtype());
        res.setTotal(cmd.getTotal());
        res.setPages(cmd.getPages());
        res.setOffset(cmd.getOffset());
        return res;
    }

    public FaceCmdRes fail(FaceCmd cmd, int code, String message) {
        FaceCmdRes res = new FaceCmdRes();
        res.setDeviceId(ServiceRegistry.getInstance().getDeviceService().getExtSN());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(code);
        res.setMsg(message);
        res.setObjtype(cmd.getObjtype());
        res.setTotal(cmd.getTotal());
        res.setPages(cmd.getPages());
        res.setOffset(cmd.getOffset());
        return res;
    }
    @Override
    public FaceCmd buildPkg(FaceCmd cmd) throws NeulinkException {
        //推送消息到达
        FaceCmd faceCmd = new FaceCmd();
        String cmdStr = cmd.getCmdStr();
        String jsonUrl = cmd.getDataUrl();
        long offset = cmd.getOffset();

        int index = jsonUrl.lastIndexOf("/");
        String baseUrl = jsonUrl.substring(0,index+1);//包含'/'

        String newJsonFileUrl = baseUrl+offset+".json";

        String body = NeuHttpHelper.dldFile2String(newJsonFileUrl,3);

        SyncInfo syncInfo = JSonUtils.toObject(body, SyncInfo.class);

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
            throw new NeulinkException(NeulinkException.CODE_50002,e.getMessage());
        }

        Map images = null;
        ActionResult<Map<String,Object>> actionResult = null;
        if(NEULINK_MODE_ADD.equalsIgnoreCase(cmdStr)||
                NEULINK_MODE_UPDATE.equalsIgnoreCase(cmdStr)||
                NEULINK_MODE_SYNC.equalsIgnoreCase(cmdStr)){
            /**
             * exit_id
             * image_type
             * face_sid
             */
            images = getImages(reqdir);//加载并计算图片的face特征值
            faceCmd.setOffset(offset);
            faceCmd.setData(params);
            faceCmd.setStringKVMap(images);

        }
        else if(NEULINK_MODE_PUSH.equalsIgnoreCase(cmdStr)){
            faceCmd.setOffset(offset);
            faceCmd.setData(params);
        }
        else if(NEULINK_MODE_DEL.equalsIgnoreCase(cmdStr)){
            faceCmd.setOffset(offset);
            faceCmd.setData(params);
        }
        /**
         * 清空图片临时目录
         */
        FileUtils.deleteDirectory(toDir.getAbsolutePath());//清空

        return faceCmd;
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
    private Map<String,Map<String,String>> getImages(String reqdir)  {

        List<String> failed = new ArrayList<String>();
        /**
         * 当前请求的所有图片信息
         */
        Map imagesData = new HashMap();

        String imagesFileDir = reqdir + "/img";
        File[] images = new File(imagesFileDir).listFiles();//读取图片列表
        int len = images==null?0:images.length;
        for(int i=0;i<len;i++){

            File tmp = images[i];
            //工号.jpg即ext_id=工号
            String tmpName = tmp.getName();
            if(tmpName==null||tmpName.trim().length()==0){
                NeuLogUtils.iTag(TAG,"图片文件为空，其不符合【ext_id.jpg】规则");
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
}
