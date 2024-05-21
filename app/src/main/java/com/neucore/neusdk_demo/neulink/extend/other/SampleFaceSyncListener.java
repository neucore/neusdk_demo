package com.neucore.neusdk_demo.neulink.extend.other;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.neucore.NeuSDK.NeuFace;
import com.neucore.NeuSDK.NeuFaceRegisterNode;
import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.impl.cmd.rrpc.FaceCmd;
import com.neucore.neulink.impl.cmd.rrpc.FaceData;
import com.neucore.neulink.impl.cmd.rrpc.FaceNode;
import com.neucore.neulink.impl.cmd.rrpc.FacePkgActionResult;
import com.neucore.neulink.impl.cmd.rrpc.KVPair.KeyEnum;
import com.neucore.neulink.impl.NeulinkEvent;
import com.neucore.neulink.impl.UpdateActionResult;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neusdk_demo.service.impl.LibManagerService;
import com.neucore.neusdk_demo.neucore.NeuFaceFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.ObjectUtil;

/**
 * 人脸同步
 */
public class SampleFaceSyncListener implements ICmdListener<FacePkgActionResult,FaceCmd> {
    final private String ADD = "add",DEL = "del",UPDATE = "update",SYNC = "sync";
    private LibManagerService libManagerService;
    private NeuFace mNeucore_face;
    private String TAG = "SampleFaceSyncListener";
    public SampleFaceSyncListener(){
        this.libManagerService = new LibManagerService(ContextHolder.getInstance().getContext());
        mNeucore_face  = NeuFaceFactory.getInstance().create();
    }
    @Override
    public FacePkgActionResult doAction(NeulinkEvent<FaceCmd> event) {
        FaceCmd faceCmd = event.getSource();
        String cmd = faceCmd.getCmd();//add：添加|del：删除|update：更新|sync：同步

        long reqTime = faceCmd.getReqtime();
        /**
         * 总包数
         */
        long pages = faceCmd.getPages();
        /**
         * 当前第几个包
         */
        long offset = faceCmd.getOffset();

        /**
         * 获取人脸描述数据
         */
        List<FaceData> params = faceCmd.getDataList();
        /**
         * FaceData 结构介绍
         * ext_id:
         * 访客规则：用逗号连接xxx,中控卡号；
         * eg:vip1,888888 表示VIP访客；
         * eg:n1,666666 表示普通访客【面试人员等】;
         * 正式员工规则：中控卡号
         *
         */
        /**
         * 扩展信息：
         * extInfo：KVPair[]
         */
        /**
         * 名单类型
         */
        KeyEnum type = KeyEnum.Type;
        /**
         * 名单起效时间：unix_timestamp
         */
        KeyEnum start = KeyEnum.PeriodStart;
        /**
         * 名单失效时间：unix_timestamp
         */
        KeyEnum end = KeyEnum.PeriodEnd;
        /**
         * key:ext_id : 卡号;
         * value: Map<String,Object> keys:ext_id,image_type,file
         */
        Map<String, Map<String,Object>> images = faceCmd.getStringKVMap();

        images = getFaceSids(images);

        List failed = (List)images.remove("failed");

        if(ADD.equalsIgnoreCase(cmd)||
                UPDATE.equalsIgnoreCase(cmd)||
                SYNC.equalsIgnoreCase(cmd)){
            //保存人脸到 twocamera/photo/ 文件夹下
            getFaceSidsAdd(images);
            /**
             * 数据库操作
             * @TODO 根据自己需要自行定义，可替换自己的代码
             */
            libManagerService.insertOrUpdFacelib(reqTime,params,images);
        }
        else if(DEL.equalsIgnoreCase(cmd)){
            //删除人脸到 twocamera/photo/ 文件夹下
            getFaceSidsDelete(params);
            /**
             * 数据库操作
             * @TODO 根据自己需要自行定义，可替换自己的代码
             */
            libManagerService.deleteFacelib(params);
        }

        /**
         * 表示当前包是最后一个数据包
         */
        if(offset==pages   //最后一个包已经处理完成
                && SYNC.equalsIgnoreCase(cmd)){ //同步以云端数据为准，设备端多余的不一致的数据执行删除操作
            /**
             * 最后一个包时，需要执行清理历史数据【无效数据】，可替换自己的代码
             * @TODO 根据自己需要自行定义，可替换自己的代码，建议根据请求时间进行清理；sample根据数据的更新时间进行处理
             */
            libManagerService.deleteFacelibByReqTime(reqTime);//删除历史数据
        }

        FacePkgActionResult result = new FacePkgActionResult();
        result.setCode(200);
        result.setMessage("success");
        result.setData(failed);

        return result;
    }

    private FaceNode getFaceNode(File tmp,BitmapFactory.Options options) throws IOException{
        byte[] image = facelibImageReader(tmp);
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        /**
         * @TODO: 算法实现图片解析
         */
        NeuFaceRegisterNode registerNode = mNeucore_face.neu_iva_get_picture_face_feature_bitmap(bitmap);
        FaceNode faceNode = new FaceNode();
        faceNode.setFeatureValid(registerNode.getFeatureValid());
        faceNode.setFaceSid(JSonUtils.toJson(registerNode.getFeature_v2()));
        faceNode.setFaceSidMask(JSonUtils.toJson(registerNode.getFeature_v2()));
        return faceNode;
    }
    /**
     * 解析每张图片获得每张图片的ext_id,image_type,face_sid,face_sid_mask
     * 当图片特征无效时，进入到failed列表内
     * @return 返回多张图片信息
     * @throws IOException
     */
    private Map getFaceSids(Map<String,Map<String,Object>> images)  {

        List<String> failed = new ArrayList<String>();
        /**
         * 当前请求的所有图片信息
         */
        Map imagesData = new HashMap();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        int len = images==null?0:images.size();
        String[] ext_ids = new String[len];
        if(len>0){
            images.keySet().toArray(ext_ids);
        }
        for(int i=0;i<len;i++){

            File tmp = (File) images.get(ext_ids[i]).get("file");
            //工号.jpg即ext_id=工号
            String tmpName = tmp.getName();
            String ext_id = tmpName.substring(0, tmpName.indexOf("."));
            Map imageData = new HashMap();
            imageData.put("ext_id", ext_id);
            imageData.put("image_type", tmpName.substring(tmpName.indexOf(".") + 1));
            imageData.put("file",tmp);
            imagesData.put(ext_id,imageData);

            try {
                /**
                 * 单张图片信息
                 *
                 */
                FaceNode faceNode = getFaceNode(tmp, options);
                if(faceNode.isFeatureValid() == false){
                    if (ObjectUtil.isNotEmpty(faceNode.getFailedReason())) {
                        LogUtils.eTag(TAG,tmpName+","+faceNode.getFailedReason());
                        failed.add(ext_id + ":" + faceNode.getFailedReason());
                    }
                    else {
                        failed.add(ext_id + ":" + "图片特征无效");
                    }
                }
                else{
                    /**
                     * 获取特征值
                     */
                    imageData.put("face_sid", faceNode.getFaceSid());
                    imageData.put("face_sid_mask", faceNode.getFaceSidMask());
                    /**
                     * 保存图片数据
                     */
                    imagesData.put(ext_id,imageData);
                }
            }
            catch(IOException ex){
                failed.add(ext_id+":"+ex.getMessage());
            }
        }
        imagesData.put("failed",failed);

        return imagesData;
    }


    private void getFaceSidsAdd(Map<String,Map<String,Object>> images)  {

        int len = images==null?0:images.size();
        String[] ext_ids = new String[len];
        if(len>0){
            images.keySet().toArray(ext_ids);
        }
        for(int i=0;i<len;i++){
            File tmp = (File) images.get(ext_ids[i]).get("file");
            //工号.jpg即ext_id=工号
            String tmpName = tmp.getName();
            String ext_id = tmpName.substring(0, tmpName.indexOf("."));
            try {
                byte[] image = facelibImageReader(tmp);
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                //保存人脸到 twocamera/photo/ 文件夹下
                bytesToImage(bitmap,ext_id);
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    private void bytesToImage(Bitmap bitmap,String ext_id){
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/twocamera/photo/" + ext_id + ".jpg";
        File file = new File(filePath);
        if (file.exists()){
            file.delete();
        }
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            LogUtils.dTag("neu", "File not found: " + e.getMessage());
        } catch (IOException e) {
            LogUtils.dTag("neu", "Error accessing file: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getFaceSidsDelete(List<FaceData> params)  {

        int size = params==null?0:params.size();
        for(int i=0;i<size;i++){
            FaceData param= params.get(i);
            String ext_id = String.valueOf(param.getExtId());
            try {
                //删除人脸到 twocamera/photo/ 文件夹下
                imageToDelete(ext_id);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    private void imageToDelete(String ext_id){
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/twocamera/photo/" + ext_id + ".jpg";
        System.out.println("  删除图片: "+filePath);
        File file = new File(filePath);
        if (file.exists()){
            file.delete();
        }
    }



    /**
     * 文件加载到内存
     * @param imageFile
     * @return
     * @throws IOException
     */
    private byte[] facelibImageReader(File imageFile) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileInputStream fis = new FileInputStream(imageFile);
        byte[] buffer = new byte[1024];
        int readed = -1;
        while ((readed=fis.read(buffer))!=-1) {
            baos.write(buffer,0,readed);
        }
        fis.close();
        baos.close();
        return baos.toByteArray();
    }
}
