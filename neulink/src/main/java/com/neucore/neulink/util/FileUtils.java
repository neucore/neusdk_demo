package com.neucore.neulink.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.neucore.neulink.NeulinkException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@SuppressLint({ "NewApi", "DefaultLocale" })
public class FileUtils {
	private static String TAG = "FileUtils";
	// 获取当前目录下所有的mp4文件
	public static Vector<String> GetVideoFileName(String fileAbsolutePath) {
		Vector<String> vecFile = new Vector<String>();
		File file = new File(fileAbsolutePath);
		File[] subFile = file.listFiles();
		if(subFile!=null){
		for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
			// 判断是否为文件夹
			if (!subFile[iFileLength].isDirectory()) {
				String filename = subFile[iFileLength].getAbsolutePath();
				// 判断是否为MP4结尾
				if (filename.trim().toLowerCase().endsWith(".mp4")
						|| filename.trim().toLowerCase().endsWith(".mkv")
						|| filename.trim().toLowerCase().endsWith(".avi")) {
					vecFile.add(filename);
				}
			}
		}
		}
		return vecFile;
	}

	// 获取当前目录下所有的圖片文件
	public static Vector<String> GetImageFileName(String fileAbsolutePath) {
		Vector<String> vecFile = new Vector<String>();
		File file = new File(fileAbsolutePath);
		File[] subFile = file.listFiles();
		if(subFile!=null){
			for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
				// 判断是否为文件夹
				if (!subFile[iFileLength].isDirectory()) {
					String filename = subFile[iFileLength].getAbsolutePath();
					// 判断是否为MP4结尾
					if (filename.trim().toLowerCase().endsWith(".jpg")) {
						vecFile.add(filename);
					}
				}
			}
		}
		return vecFile;
	}

	/**
	 * 保存文件
	 * 
	 * @throws IOException
	 */
//	static int count=0;
	public static int getBitmapSize(Bitmap bitmap){
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){    //API 19
	        return bitmap.getAllocationByteCount();
	    }
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1){//API 12
	        return bitmap.getByteCount();
	    }
	    return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
	}
	public static byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        //把所有的变量收集到一起，然后一次性把数据发送出去
        byte[] buffer = new byte[1024]; // 用数据装
        int len = 0;
        while ((len = is.read(buffer)) != -1) {
            outstream.write(buffer, 0, len);
        }
        outstream.close();

        return outstream.toByteArray();
    }
	/**
	 * Get image from newwork
	 * 
	 * @param path
	 *            The path of image
	 * @return InputStream
	 * @throws Exception
	 */
	public static InputStream getImageStream(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		int num=conn.getContentLength()/1024;
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			return conn.getInputStream();
		}else{
			return null;
		}
	}
	
	public static Bitmap getImageBitmap(String path,boolean isAd) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(20 * 1000);
		conn.setRequestMethod("GET");
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
		InputStream is = conn.getInputStream();
	     BitmapFactory.Options options=new BitmapFactory.Options();
			int num=1;
			try{
				if(conn.getContentLength()>500000) {
//				num = conn.getContentLength() / 200000;
					num=Chufa(conn.getContentLength(),500000);
					options.inSampleSize =num;
				}
			}catch (Exception e){
				Log.e(TAG,"getImageBitmap",e);
			}
	     options.inJustDecodeBounds = false;
          /*  int sign=1;
	     if(num>100)  sign = num / 30;
             if (!isAd && sign > 0)
                 options.inSampleSize = sign;   //width，hight设为原来的2分一*/
	     Bitmap bm =BitmapFactory.decodeStream(is,null,options);
//	     LogUtils.i("TAG","原始："+num+"压缩："+sign+"压缩后："+bm.getByteCount()/1024.);
		 return bm;
		}else{
			return null;
		}
	}
	//定义方法
	public static int Chufa(int C,int D) {
		BigDecimal a = new BigDecimal(C);
		BigDecimal b = new BigDecimal(D);
		return Integer.parseInt(a.divide(b,0,BigDecimal.ROUND_HALF_UP)+"");
	}
	public static Bitmap getImageBitmap2(String path,boolean isAd) throws Exception {
		byte[] b=null;
		URL url = new URL(path);
//		URL url = new URL("http://login.zzumi.com/txy/upload//headPic//190308133616//15916497472.jpg");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(20 * 1000);
		conn.setRequestMethod("GET");
		int num=conn.getContentLength()/1024;
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			InputStream is = conn.getInputStream();
			b=readInputStream(is);
			Bitmap bm =decodeSampledBitmapFromStream(b,300,300);
			return bm;
		}else{
			return null;
		}
	}
	/**
	 * 加载本地图片
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getLoacalBitmap(String url) {
		try {
			if(TextUtils.isEmpty(url))return null;
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis); // /把流转化为Bitmap图片
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		}
	}

	static Bitmap comp(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出   
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		newOpts.inPreferredConfig = Config.RGB_565;// 降低图片从ARGB888到RGB565
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	private static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			options -= 10;// 每次都减少10
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中

		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}
	/**
     * 递归删除文件和文件夹
     * @param file    要删除的根目录
     */
    public static void RecursionDeleteFile(File file){
        if(file.isFile()){
            file.delete();
            return;
        }
        if(file.isDirectory()){
            File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0){
                file.delete();
                return;
            }
            for(File f : childFile){
                RecursionDeleteFile(f);
            }
            file.delete();
        }
    }

	//root自动安装应用
		public static int execRootCmdSilent(String cmd) {  
		       int result = -1;  
		       DataOutputStream dos = null;  
		  
		       try {  
		           Process p = Runtime.getRuntime().exec("su");  
		           dos = new DataOutputStream(p.getOutputStream());  
		  
		           Log.i("TAG", cmd);  
		           dos.writeBytes(cmd + "\n");  
		           dos.flush();  
		           dos.writeBytes("exit\n");  
		           dos.flush();  
		           p.waitFor();  
		           result = p.exitValue();  
		       } catch (Exception e) {
				   Log.e(TAG, e.getMessage(), e);
		       } finally {  
		           if (dos != null) {  
		               try {  
		                   dos.close();  
		               } catch (Exception e) {  

		               }  
		           }  
		       }  
		       return result;  
		   }
	private static byte[] readInputStream(InputStream in) throws Exception{
		int len=0;
		byte buf[]=new byte[1024];
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		while((len=in.read(buf))!=-1){
			out.write(buf,0,len);  //把数据写入内存
		}
		out.close();  //关闭内存输出流
		return out.toByteArray(); //把内存输出流转换成byte数组
	}
	private static Bitmap decodeSampledBitmapFromStream(byte[] b,int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(b, 0, b.length, options);
		// 调用上面定义的方法计算inSampleSize值
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		// 使用获取到的inSampleSize值再次解析图片
		options.inJustDecodeBounds = false;
		Log.v("decode","返回bitmap");
		return BitmapFactory.decodeByteArray(b, 0, b.length, options);
	}
	/*private static int calculateInSampleSize(BitmapFactory.Options options,
									  int reqWidth, int reqHeight) {
		// 源图片的高度和宽度
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			// 计算出实际宽高和目标宽高的比率
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			// 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
			// 一定都会大于等于目标的宽和高。
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		Log.v("calculate"," "+inSampleSize);
		return inSampleSize;
	}*/
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}
	/**
	 * 把字节数组保存为一个文件
	 *
	 * @param b
	 * @param outputFile
	 * @return
	 */
	public static File getFileFromBytes(byte[] b, String outputFile) {
		File ret = null;
		BufferedOutputStream stream = null;
		try {
			ret = new File(outputFile);
			FileOutputStream fstream = new FileOutputStream(ret);
			stream = new BufferedOutputStream(fstream);
			stream.write(b);
		} catch (Exception e) {
			// log.error("helper:get file from byte process error!");
			Log.e(TAG, e.getMessage(), e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					// log.error("helper:get file from byte process error!");
				}
			}
		}
		return ret;
	}

	public static void move(File from,File to){
		FileInputStream fileInputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			fileInputStream = new FileInputStream(from);
			fileOutputStream = new FileOutputStream(to);
			byte[] buffer = new byte[1024];
			int readed = 0;
			while ((readed = fileInputStream.read(buffer)) != -1) {
				fileOutputStream.write(buffer, 0, readed);
			}
		}
		catch (Exception ex){
			throw new NeulinkException(500,ex.getMessage());
		}
		finally {
			if(fileInputStream!=null){
				try {
					fileInputStream.close();
				} catch (IOException e) {
				}
			}
			if(fileOutputStream!=null){
				try {
					fileOutputStream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void zip(String inputFile, String outputFile) throws IOException {
		FileOutputStream fileOutputStream = null;
		ZipOutputStream out = null;
		BufferedOutputStream bos = null;
		try {
			new File(outputFile).createNewFile();
			fileOutputStream = new FileOutputStream(outputFile);
			//创建zip输出流
			out = new ZipOutputStream(fileOutputStream);
			//创建缓冲输出流
			bos = new BufferedOutputStream(out);

			File input = new File(inputFile);
			zip(out, bos, input,null);
		}
		finally {
			try {
				if (bos != null) {
					bos.close();
				}
			}
			catch (Exception e){}
			try{
				if(out!=null) {
					out.close();
				}
			}
			catch (Exception e){}
			try {
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}
			}
			catch (Exception e){}
		}

	}
	/**
	 * @param name 压缩文件名，可以写为null保持默认
	 */
	//递归压缩
	private static void zip(ZipOutputStream out, BufferedOutputStream bos, File input, String name) throws IOException {
		if (name == null) {
			name = input.getName();
		}
		//如果路径为目录（文件夹）
		if (input.isDirectory()) {
			//取出文件夹中的文件（或子文件夹）
			File[] flist = input.listFiles();

			if (flist.length == 0)//如果文件夹为空，则只需在目的地zip文件中写入一个目录进入
			{
				out.putNextEntry(new ZipEntry(name + "/"));
			} else//如果文件夹不为空，则递归调用compress，文件夹中的每一个文件（或文件夹）进行压缩
			{
				for (int i = 0; i < flist.length; i++) {
					zip(out, bos, flist[i], name + "/" + flist[i].getName());
				}
			}
		} else//如果不是目录（文件夹），即为文件，则先写入目录进入点，之后将文件写入zip文件中
		{
			out.putNextEntry(new ZipEntry(name));
			FileInputStream fos = new FileInputStream(input);
			BufferedInputStream bis = new BufferedInputStream(fos);
			int len;
			//将源文件写入到zip文件中
			byte[] buf = new byte[1024];
			while ((len = bis.read(buf)) != -1) {
				bos.write(buf,0,len);
			}
			bis.close();
			fos.close();
		}
	}
	/**
	 * 解压缩文件
	 * @param fileName
	 * @param toDir
	 * @throws IOException
	 */
	public static void unzipFile(File fileName, String toDir) throws IOException {
		ZipFile zipFile = null;
		try{
			zipFile = new ZipFile(fileName);
			Enumeration<?> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				// 如果是文件夹，就创建个文件夹
				if (entry.isDirectory()) {
					String dirPath = toDir + "/" + entry.getName();
					File dir = new File(dirPath);
					dir.mkdirs();
				} else {
					// 如果是文件，就先创建一个文件，然后用io流把内容copy过去
					File targetFile = new File(toDir + "/" + entry.getName());
					// 保证这个文件的父文件夹必须要存在
					if(!targetFile.getParentFile().exists()){
						targetFile.getParentFile().mkdirs();
					}
					targetFile.createNewFile();
					// 将压缩文件内容写入到这个文件中
					InputStream is = null;
					FileOutputStream fos = null;
					try {
						is = zipFile.getInputStream(entry);
						fos = new FileOutputStream(targetFile);
						int len;
						byte[] buf = new byte[1024];
						while ((len = is.read(buf)) != -1) {
							fos.write(buf, 0, len);
						}
					}
					catch (IOException ex){
						throw ex;
					}
					finally {
						if(fos!=null) {
							fos.close();
						}
						if(is!=null) {
							is.close();
						}
					}
				}
			}
		}
		catch (IOException e) {
			throw e;
		}
		finally {
			if(zipFile != null){
				try {
					zipFile.close();

				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 删除文件，可以是文件或文件夹
	 *
	 * @param fileName
	 *            要删除的文件名
	 * @return 删除成功返回true，否则返回false
	 */
	public static boolean delete(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			System.out.println("删除文件失败:" + fileName + "不存在！");
			return false;
		} else {
			if (file.isFile())
				return deleteFile(fileName);
			else
				return deleteDirectory(fileName);
		}
	}

	/**
	 * 删除单个文件
	 *
	 * @param fileName
	 *            要删除的文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		// 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				//System.out.println("删除单个文件" + fileName + "成功！");
				return true;
			} else {
				//System.out.println("删除单个文件" + fileName + "失败！");
				return false;
			}
		} else {
			//System.out.println("删除单个文件失败：" + fileName + "不存在！");
			return false;
		}
	}

	/**
	 * 删除目录及目录下的文件
	 *
	 * @param dir
	 *            要删除的目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	public static boolean deleteDirectory(String dir) {
		// 如果dir不以文件分隔符结尾，自动添加文件分隔符
		if (!dir.endsWith(File.separator))
			dir = dir + File.separator;
		File dirFile = new File(dir);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
			//System.out.println("删除目录失败：" + dir + "不存在！");
			return false;
		}
		boolean flag = true;
		// 删除文件夹中的所有文件包括子目录
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
			// 删除子目录
			else if (files[i].isDirectory()) {
				flag = deleteDirectory(files[i]
						.getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag) {
			//System.out.println("删除目录失败！");
			return false;
		}
		// 删除当前目录
		if (dirFile.delete()) {
			//System.out.println("删除目录" + dir + "成功！");
			return true;
		} else {
			return false;
		}
	}
}
