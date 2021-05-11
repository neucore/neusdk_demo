package com.neucore.neusdk_demo.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.Surface;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class HelpUtil {
    private static String TAG = "HelpUtil";


    /**
     * bitmap旋转90度
     *
     * @param bitmap
     * @return
     */
    public static Bitmap createRotateBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            Matrix m = new Matrix();
            try {
                m.setRotate(90, bitmap.getWidth() / 2, bitmap.getHeight() / 2);// 90就是我们需要选择的90度
                Bitmap bmp2 = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                bitmap.recycle();
                bitmap = bmp2;
            } catch (Exception ex) {
                System.out.print("创建图片失败！" + ex);
            }
        }
        return bitmap;
    }

    public static Bitmap getBitmapByUri(Uri uri, ContentResolver cr) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(cr
                    .openInputStream(uri));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e(TAG, e.getMessage(), e);
            bitmap = null;
        }
        return bitmap;
    }

    /**
     * 获取格式化日期字符串
     *
     * @param date
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDateFormatString(Date date) {
        if (date == null)
            date = new Date();
        String formatStr = new String();
        SimpleDateFormat matter = new SimpleDateFormat("yyyyMMdd_HHmmss");
        formatStr = matter.format(date);
        return formatStr;
    }

    // 提供一个静态方法，用于根据手机方向获得相机预览画面旋转的角度
    public static int getPreviewDegree(Activity activity) {
        // 获得手机的方向
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degree = 0;
        // 根据手机的方向计算相机预览画面应该选择的角度
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 90;
                break;
            case Surface.ROTATION_90:
                degree = 0;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 180;
                break;
        }
        return degree;
    }

    /**
     * 通过Base32将Bitmap转换成Base64字符串
     *
     * @param bit
     * @return
     */
    public static String Bitmap2StrByBase64(Bitmap bit) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bit.compress(CompressFormat.JPEG, 40, bos);//参数100表示不压缩
        byte[] bytes = bos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    /**
     * 通过Base32将Base64字符串转换成Bitmap
     *
     * @return
     */
    public static Bitmap getBitmap(String imgBase64Str) {
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(imgBase64Str, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;

    }

    /**
     * 通过BASE64Decoder解码，并生成图片
     *
     * @param imgStr 解码后的string
     */
    public static Bitmap string2Image(String imgStr) {
        // 对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null)
            return null;
        try {
            // Base64解码
            byte[] b = Base64.decode(imgStr.getBytes(), Base64.DEFAULT);
//	            byte[] b = new BASE64Decoder().decodeBuffer(imgStr);  
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    // 调整异常数据
                    b[i] += 256;
                }
            }
            return BitmapFactory.decodeByteArray(b, 0, b.length);
	           /* // 生成Jpeg图片  
	            OutputStream out = new FileOutputStream(imgFilePath);  
	            out.write(b);  
	            out.flush();  
	            out.close();  */
        } catch (Exception e) {
            return null;
        }
    }

    //图片压缩
    public static Bitmap byteToBitmap(byte[] imgByte) {
        InputStream input = null;
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
//		options.inSampleSize = 2;
        input = new ByteArrayInputStream(imgByte);
        SoftReference softRef = new SoftReference(BitmapFactory.decodeStream(
                input, null, options));
        bitmap = (Bitmap) softRef.get();
        if (imgByte != null) {
            imgByte = null;
        }

        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e(TAG, e.getMessage(), e);
        }
        return bitmap;
    }

    /*SMS.sendurl=http://s.cloudsms.cc/


       SMS.account=njzktz
       SMS.pwd=njzktz*/
    public static void run(String mobile, String content) {
//			String sendurl = getSMSUrl();
        String sendurl = "http://s.cloudsms.cc/";
//			String account = getSMSAccount();
        String account = "njzktz";
//			String pwd = getSMSPwd();
        String pwd = "njzktz";
//			String content = "【南部路桥】您有一条未读的公告通知,标题："+notice.getTitle();
        StringBuffer sb = new StringBuffer();
        try {
            int maxTimes = 3;// 如果发送短信失败，最多循环3次
            for (int i = 0; i < maxTimes; i++) {
                sb = new StringBuffer();
                String urlString = sendurl + "send?account=" + account + "&pwd="
                        + pwd + "&mobiles=" + mobile + "&content="
                        + URLEncoder.encode(content, "UTF-8");
                URL url = new URL(urlString);
                URLConnection conn = url.openConnection();
                conn.setReadTimeout(2000);
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                for (String line = null; (line = reader.readLine()) != null; ) {
                    sb.append(line);
                }
                reader.close();
                System.out.println("号码：" + mobile);
                System.out.println("发送短信返回结果：" + sb.toString());
                JSONObject subjectJson = new JSONObject(sb.toString());
                int result = subjectJson.getInt("result");
                if (result == 1) {// 发送成功
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("短信发送失败:" + e.getMessage());
        }
    }


    private String getSMSAccount() {
        Properties prop = new Properties();
        String key = "njzktz";
        try {
            InputStream in = getClass().getResourceAsStream(
                    "/openfire.properties");
            prop.load(in);
            key = prop.getProperty("SMS.account").trim();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return key;
    }

    /*
     * 判断服务是否启动,context上下文对象 ，className服务的name
     */
    public static boolean isServiceRunning(Context mContext, String className) {

        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(30);

        if (!(serviceList.size() > 0)) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    /*
     * 添加水印
     */
    public static Bitmap addDate(Bitmap bitmap) {
//	    	首先，我们需要知道水印绘制的具体位置，就需要得到图片宽高，而android中的bitmap类就给我们提供了这个方法：
        int width = bitmap.getWidth();
        int hight = bitmap.getHeight();
        Bitmap imgTemp = Bitmap.createBitmap(width, hight, Bitmap.Config.ARGB_8888);
//	    	得到了图片的宽高我们就需要创建画笔和画布，用于绘制，并对图片做一些简单处理：
        Canvas canvas = new Canvas(imgTemp);// 初始化画布绘制的图像到icon上
        Paint photoPaint = new Paint(); // 建立画笔
        photoPaint.setDither(true); // 获取更清晰的图像采样
        photoPaint.setFilterBitmap(true);// 过滤一些
//	    	然后我们就需要绘制矩形，并对画笔做一些简单的设置：
        Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());// 创建一个指定的新矩形的坐标
        Rect dst = new Rect(0, 0, width, hight);// 创建一个指定的新矩形的坐标
        canvas.drawBitmap(bitmap, src, dst, photoPaint);// 将photo 缩放或则扩大到        dst使用的填充区photoPaint
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);// 设置画笔
        textPaint.setTextSize(35.0f);// 字体大小
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);// 采用默认的宽度
        textPaint.setColor(Color.GREEN);// 采用的颜色
//	    	（设置画笔可以设置更多属性，看自己喜好）
//	    	之后我们就可以开始绘制文字了（这里我就绘制当前日期了）：
        canvas.drawText(String.valueOf("照片日期：" + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(
                new Date(System.currentTimeMillis()))), 20, 65, textPaint);// 绘制上去字，开始未知x,y采用那只笔绘制
//	    	最后我们只需要使用画布给图片加水印就可以了：
        canvas.drawBitmap(bitmap, bitmap.getWidth() - 5, bitmap.getHeight() - 5, textPaint);// 在src的右下角画入水印
        canvas.save();//Canvas.ALL_SAVE_FLAG
        canvas.restore();
        return bitmap;
    }

    public static boolean getStatus(String start, String end) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            Date curDates = new Date(System.currentTimeMillis());//  获取当前时间
            String strs = format.format(curDates);
            String[] dds = new String[]{};
            //  分取系统时间 小时分
            dds = strs.split(":");
            if ("".equals(dds[0]) || "".equals(dds[1])) return false;
            int dhs = Integer.parseInt(dds[0]);
            int dms = Integer.parseInt(dds[1]);
            String[] st = new String[]{};
            st = start.split(":");
            String[] et = new String[]{};
            et = end.split(":");
            if ("".equals(st[0]) || "".equals(st[1]) || "".equals(et[0]) || "".equals(et[1]))
                return false;
            // 开始时间
            int sth = Integer.parseInt(st[0]);// 小时
            int stm = Integer.parseInt(st[1]);// 分
            // 结束时间 
            int eth = Integer.parseInt(et[0]);// 小时
            int etm = Integer.parseInt(et[1]);// 分
            if (sth <= dhs && dhs <= eth) {
                if (sth == dhs && dms < stm) {
                    return false;
                } else if (dhs == eth && dms > etm) {
                    return false;
                } else {
                    return true;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return false;
    }

    public static String changeDex(String ten_str) {
        String cardno = "";
        try {
            //10进制转16进制
            String str = Long.toHexString(Long.parseLong(ten_str));
            List<String> list = new ArrayList<String>();
            int size_num = str.length() + 2;
            for (int i = 2; i < size_num; i += 2) {
                if (size_num > i)
                    list.add(str.substring(i - 2, i));
            }
            Collections.reverse(list);
            Iterator<String> iter = list.iterator();
            String str_new = "";
            while (iter.hasNext()) {
                str_new += iter.next();
            }

            long cardLong = Long.parseLong(str_new, 16);
            if ((cardLong + "").length() < 10) {
                cardno = String.format("%010d", cardLong);
            } else {
                cardno = cardLong + "";
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return cardno;
    }

    //将assets目录下的nb 文件夹中的xx.nb 文件放入 /storage/emulated/0/neucore/nb/S905D3/
    public static void copyAssetResource2File(Context context)
    {
        Log.d(TAG, "开始拷贝nb文件");
        long begin = System.currentTimeMillis();

        String OrgFilepath = "/storage/emulated/0/neucore/";
        String ModelFilepath = "/storage/emulated/0/neucore/nb/";
        String cameraPath = "/storage/emulated/0/neucore/camera/";

        try {
            //判断 /storage/emulated/0/neucore/ 路径是否存在
            File orgpath = new File(OrgFilepath);
            if(! orgpath.exists()){
                orgpath.mkdirs();
            }

            //判断 /storage/emulated/0/neucore/nb/ 路径是否存在
            File despath = new File(ModelFilepath);
            if(! despath.exists()){
                despath.mkdirs();
            }

            //判断 /storage/emulated/0/neucore/camera/ 路径是否存在
            File caPath = new File(cameraPath);
            if(! caPath.exists()){
                caPath.mkdirs();
            }

            /**
             * 下面代码将assets目录下 nb 文件夹下所有的文件拷贝到定义的 /storage/emulated/0/neucore/nb/ 路径下
             * 可在此添加限制,只在apk首次启动或者 /storage/emulated/0/neucore/nb/路径被删除 时做此操作,
             * 达到节约时间目的
             */

            //for S905D3
            String ModelFileName = "nb/S905D3.bak/";
            String tmp_ModelFilepath = ModelFilepath + "S905D3/";
            File tmp_path = new File(tmp_ModelFilepath);
            if(! tmp_path.exists()){
                tmp_path.mkdirs();
            }

            String[] filenames = context.getAssets().list(ModelFileName);
            for (String file : filenames) {
                InputStream is = context.getAssets().open(ModelFileName + file);

                File outF = new File(tmp_ModelFilepath+file);
                FileOutputStream fos = new FileOutputStream(outF);

                int byteCount;
                byte[] buffer = new byte[1024];
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                is.close();
                fos.close();
                outF.setReadable(true);
            }

            //for A311D
            ModelFileName = "nb/A311D/";
            tmp_ModelFilepath = ModelFilepath + "A311D/";
            tmp_path = new File(tmp_ModelFilepath);
            if(! tmp_path.exists()){
                tmp_path.mkdirs();
            }

            filenames = context.getAssets().list(ModelFileName);
            for (String file : filenames) {
                InputStream is = context.getAssets().open(ModelFileName + file);

                File outF = new File(tmp_ModelFilepath+file);
                FileOutputStream fos = new FileOutputStream(outF);

                int byteCount;
                byte[] buffer = new byte[1024];
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                is.close();
                fos.close();
                outF.setReadable(true);
            }

            Log.d(TAG,"NEUCORE 拷贝nb文件结束, copyAssetResource2File cost " + (System.currentTimeMillis() - begin)+" ms");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createFileNB(){
        String OrgFilepath = "/storage/emulated/0/neucore/";
        String ModelFilepath0 = "/storage/emulated/0/neucore/nb/";
        String ModelFilepath = "/storage/emulated/0/neucore/nb/S905D3/";

        //判断 /storage/emulated/0/neucore/ 路径是否存在
        File orgpath = new File(OrgFilepath);
        if (orgpath.exists()){
            orgpath.delete();
        }
        if(! orgpath.exists()){
            orgpath.mkdirs();
        }

        //判断 /storage/emulated/0/neucore/nb/ 路径是否存在
        File despath0 = new File(ModelFilepath0);
        if(! despath0.exists()){
            despath0.mkdirs();
        }

        //判断 /storage/emulated/0/neucore/nb/S90503/ 路径是否存在
        File despath = new File(ModelFilepath);
        if(! despath.exists()){
            despath.mkdirs();
        }
    }
}

