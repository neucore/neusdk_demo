package com.neucore.neusdk_demo.utils;

import android.app.Activity;
import android.util.Log;


import org.apache.http.util.EncodingUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileAccess extends Activity {

	private static String TAG = "FileAccess";

	/**
	 * 一、私有文件夹下的文件存取（/data/data/包名/files）
	 * 
	 * @param fileName
	 * @param message
	 */
	public void writeFileData(String fileName, String message) {
		try {
			FileOutputStream fout = openFileOutput(fileName, MODE_PRIVATE);
			byte[] bytes = message.getBytes();
			fout.write(bytes);
			fout.close();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	/**
	 * //读文件在./data/data/包名/files/下面
	 * 
	 * @param fileName
	 * @return
	 */
	public String readFileData(String fileName) {
		String res = "";
		try {
			FileInputStream fin = openFileInput(fileName);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
			fin.close();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return res;
	}

	/**
	 * 写， 读sdcard目录上的文件，要用FileOutputStream， 不能用openFileOutput
	 * 不同点：openFileOutput是在raw里编译过的，FileOutputStream是任何文件都可以
	 * 
	 * @param fileName
	 * @param message
	 */
	// 写在/mnt/sdcard/目录下面的文件
	public static void writeFileSdcard(String album,String fileName, String message) {
		try {
			// FileOutputStream fout = openFileOutput(fileName, MODE_PRIVATE);
			File dir = new File(album);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fout = new FileOutputStream(fileName);
			byte[] bytes = message.getBytes();
			fout.write(bytes);
			fout.close();
		}
		catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}
	// 写在/mnt/sdcard/目录下面的文件
		public static void writeFileSdcard(String album,String fileName, byte[] bytes) {
			try {
				// FileOutputStream fout = openFileOutput(fileName, MODE_PRIVATE);
				File dir = new File(album);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				File file = new File(fileName);
				if (!file.exists()) {
					file.createNewFile();
				}
				FileOutputStream fout = new FileOutputStream(fileName);
				fout.write(bytes);
				fout.close();
			}
			catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}

	// 读在/mnt/sdcard/目录下面的文件

	public static String readFileSdcard(String fileName) {
		String res = "";
		try {
			if (!new File(fileName).exists())
				return "";
			FileInputStream fin = new FileInputStream(fileName);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
			fin.close();
		}catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return res;

	}
	// 读在/mnt/sdcard/目录下面的文件
//	public static String getPicData(String fileName) {
//		String res = "";
//		try {
//			if (!new File(fileName).exists())
//				return "";
//			LogUtils.i("TAG","压缩前："+new File(fileName).length());
//			Bitmap bitmap = BitmapCompressor.decodeSampledBitmapFromFile(
//					fileName, 800, 700);
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
//			byte[] data = baos.toByteArray();
//			res = android.util.Base64.encodeToString(data,
//					android.util.Base64.DEFAULT);
//			LogUtils.i("TAG","压缩后："+res.getBytes().length);
//			/*
//			 * FileInputStream fin = new FileInputStream(fileName); int length =
//			 * fin.available(); byte[] buffer = new byte[length];
//			 * fin.read(buffer);
//			 *
//			 * res = EncodingUtils.getString(buffer, "UTF-8"); fin.close();
//			 */
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return res;
//	}
	/**
	 * 二、从resource中的raw文件夹中获取文件并读取数据（资源文件只能读不能写）
	 * 
	 * @param fileInRaw
	 * @return
	 */
	public String readFromRaw(int fileInRaw) {
		String res = "";
		try {
			InputStream in = getResources().openRawResource(fileInRaw);
			int length = in.available();
			byte[] buffer = new byte[length];
			in.read(buffer);
			res = EncodingUtils.getString(buffer, "GBK");
			// res = new String(buffer,"GBK");
			in.close();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return res;
	}

	/**
	 * 三、从asset中获取文件并读取数据（资源文件只能读不能写）
	 * 
	 * @param fileName
	 * @return
	 */
	public String readFromAsset(String fileName) {
		String res = "";
		try {
			InputStream in = getResources().getAssets().open(fileName);
			int length = in.available();
			byte[] buffer = new byte[length];
			in.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return res;
	}

}
