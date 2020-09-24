package com.neucore.neusdk_demo.neulink.extend;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.neucore.NeuSDK.NeuFace;
import com.neucore.NeuSDK.NeuFaceRegisterNode;
import com.neucore.neulink.extend.ICmdListener;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.extend.UpdateResult;
import com.neucore.neulink.rrpc.FaceCmd;
import com.neucore.neulink.rrpc.FaceData;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neusdk_demo.neucore.NeuFaceFactory;
import com.neucore.neusdk_demo.db.LibManagerService;

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

public class SampleFaceListener implements ICmdListener<UpdateResult> {
    final private String ADD = "add",DEL = "del",UPDATE = "update",SYNC = "sync";
    private LibManagerService libManagerService;
    private NeuFace mNeucore_face;
    private String TAG = "SampleFaceListener";
    public SampleFaceListener(){
        this.libManagerService = new LibManagerService(ContextHolder.getInstance().getContext());
    }
    @Override
    public UpdateResult doAction(NeulinkEvent event) {
        FaceCmd faceCmd = (FaceCmd) event.getSource();
        String cmd = faceCmd.getCmd();//add：添加|del：删除|update：更新|sync：同步
        mNeucore_face  = NeuFaceFactory.getInstance().create();
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
        List<FaceData> params = faceCmd.getPayload();
        /**
         * key:ext_id : 卡号;
         * value: Map<String,Object> keys:ext_id,image_type,file
         */
        Map<String, Map<String,Object>> images = faceCmd.getImageDatas();

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

        UpdateResult result = new UpdateResult();
        result.setCode(200);
        result.setMessage("success");
        //返回失败的人脸ext_id 列表
        Map<String,Object> datas = new HashMap<String,Object>();
        datas.put("failed",failed);
        result.setDatas(datas);

        return result;
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
                byte[] image = facelibImageReader(tmp);

                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                //保存人脸到 twocamera/photo/ 文件夹下
                //bytesToImage(bitmap,ext_id);
                /**
                 * 单张图片信息
                 *
                 */
                NeuFaceRegisterNode faceRegisterNode = getImageFaceSid(bitmap, options);
                if(faceRegisterNode.getFeatureValid() == false){
                    Log.e(TAG,tmpName+",图片特征无效");
                    failed.add(ext_id+":"+"图片特征无效");
                }
                else{
                    /**
                     * 获取特征值
                     */
                    String[] faces = new String[]{JSonUtils.toJson(faceRegisterNode.getFeature()),JSonUtils.toJson(faceRegisterNode.getFeature())};
                    if (faces != null && faces.length > 0) {
                        imageData.put("face_sid", faces[0]);
                    }
                    if (faces != null && faces.length > 1) {
                        imageData.put("face_sid_mask", faces[1]);
                    }
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
            Log.d("neu", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("neu", "Error accessing file: " + e.getMessage());
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
     * 计算图片faceId
     * @param image
     * @param options
     * @return
     * @throws IOException
     */
    private NeuFaceRegisterNode getImageFaceSid(Bitmap image, BitmapFactory.Options options) {
        NeuFaceRegisterNode registerNode = mNeucore_face.neu_iva_get_picture_face_feature_bitmap(image);
        return registerNode;
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
