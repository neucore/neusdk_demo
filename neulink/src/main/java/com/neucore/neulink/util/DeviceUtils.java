
package com.neucore.neulink.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.CpuUsageInfo;
import android.os.Environment;
import android.os.Parcel;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.cmd.msg.DiskInfo;
import com.neucore.neulink.impl.cmd.msg.SDInfo;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import java.util.UUID;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 *
 * @author Jeff 2013-11-23
 */
public class DeviceUtils implements NeulinkConst{

	private static String TAG = TAG_PREFIX+"DeviceUtils";
	public static final int SDCARD_TYPE = 0;			//当前的日志记录类型为存储在SD卡下面
	public static final int DISK_TYPE = 1;			//当前的日志记录类型为存储在磁盘中

	private static String getFileRoot(Context context) {
		File root = null;
		String path = null;
		boolean sdCardExist = true;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			sdCardExist = Environment.getExternalStorageState()
					.equals(Environment.MEDIA_MOUNTED);
		}
		int type = getStoreType();
		if (type == SDCARD_TYPE|| sdCardExist) {
			root = Environment.getExternalStorageDirectory();
			path = root.toString();
			if (Build.VERSION.SDK_INT < 23) {
				if (path.equals("/storage/emulated/0")) {
					path = "/storage/sdcard0";
				}
			}
		}
		else{
			root = context.getCacheDir();
			path = root.toString();
		}
		return path;
	}

	public static String getNeucore(Context context){
		String rootPath = getFileRoot(context);

		if(rootPath.endsWith("/")){
			rootPath = rootPath.substring(0,rootPath.length()-1);
		}
		String sub = "neucore";
		mkidrs(rootPath,sub);
		String path = rootPath+File.separator+sub;
		return path;
	}

	private static String mkidrs(String path,String sub){
		File file = new File(path, sub);
		file.mkdirs();
		return file.getAbsolutePath();
	}

	public static String getExternalFilesDir(Context context){
		return getNeucore(context);
	}

	public static String getFilesDir(Context context){
		return getNeucore(context);
	}

	public static String getExternalCacheDir(Context context){
		return getNeucore(context);
	}

	public static String getCacheDir(Context context){
		return getNeucore(context);
	}

	private static String getFilesPath(Context context){
		int type = getStoreType();
		String path = null;
		if(type== DISK_TYPE){
			return getFilesDir(context);
		}
		else{
			return getExternalFilesDir(context);
		}
	}

	private static String getCachePath(Context context){
		int type = getStoreType();
		String path = null;
		if(type== DISK_TYPE){
			path = getCacheDir(context);
		}
		else{
			path = getExternalCacheDir(context);
		}
		new File(path).mkdirs();
		return path;
	}

	public static String getTmpPath(Context context){
		String path = getCachePath(context);
		return mkidrs(path,"temp");
	}

	public static String getLogPath(Context context){
		String path = getCachePath(context);
		return mkidrs(path,"logs");
	}

	public static String getDBPath(Context context){
		String path = getFilesPath(context);
		return mkidrs(path,"databases");
	}

	public static String getConfigPath(Context context){
		String path = getFilesPath(context);
		return mkidrs(path,"config");
	}

	/**
	 * 获取当前应存储在内存中还是存储在SDCard中
	 * @return
	 */
	public static int getStoreType(){
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return DISK_TYPE;
		}else{
			return SDCARD_TYPE;
		}
	}
    /**
     *
     * @return
     */
    public static String getBrand() {
        return android.os.Build.BRAND;
    }

    /**
     *
     * @return
     */
    public static String getModel() {
        return android.os.Build.MODEL;
    }

    /**
     * IMEI
     *
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
	public static String getImei(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }
    /**
     * @return
     */
    public static String getOsVersion() {
        return "android" + android.os.Build.VERSION.SDK_INT;
    }

	protected static volatile UUID uuid;

	/**
	 * 默认设备Id的获取规则<br/>
	 * 先读取cpu-sn；如果有则返回；<br/>
	 * 如果为空，则读取有线网络的mac地址，不为空直接返回；<br/>
	 * 如果为空，则读取Wi-Fi的mac地址，不为空直接返回；<br/>
	 * 如果mac地址为空，则读取ANDROID_ID返回；<br/>
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context){
		String deviceId = getCPUSN(context);
		if("0000000000000000".equalsIgnoreCase(deviceId)){
			deviceId = getMacAddress();
			if(ObjectUtil.isNotEmpty(deviceId)){
				deviceId = deviceId.replace(":","");
			}
			if(ObjectUtil.isEmpty(deviceId)){
				deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
			}
		}
		return deviceId;
	}

	private static String getCPUSN(Context context){

		String str = "", strCPU = "", cpuAddress = "0000000000000000";
		try {
			Map<String,String> result = ShellExecutor.execute(context,"cat /proc/cpuinfo");
			String success = result.get("success");
			if("1".equals(success)){
				//读取CPU信息
				String stdout = result.get("stdout");
				InputStreamReader ir = new InputStreamReader(new BufferedInputStream(new ByteArrayInputStream(stdout.getBytes(StandardCharsets.UTF_8))));
				LineNumberReader input = new LineNumberReader(ir);
				//查找CPU序列号
				for (int i = 1; i < 100; i++) {
					str = input.readLine();
					if (str != null) {
						//查找到序列号所在行
						if (str.indexOf("Serial") > -1) {
							//提取序列号
							strCPU = str.substring(str.indexOf(":") + 1,
									str.length());
							//去空格
							cpuAddress = strCPU.trim();
							break;
						}
					} else {
						//文件结尾
						break;
					}
				}
				input.close();
			}
		} catch (Exception e) {
			//赋予默认值
			NeuLogUtils.eTag(TAG, e.getMessage(), e);
		}
		return cpuAddress;
	}

	public static String getNpuMode(Context context){
		String str = "", strNPU = "", npuVersion = "0000000000000000";
		try{
			String dir = getCacheDir(context);
			File file = new File(dir+File.separator+"npu");
			if(file.exists()){
				BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
				npuVersion = bufferedReader.readLine();
				bufferedReader.close();
			}
			else{
				Map<String,String> result = ShellExecutor.execute(context,"dmesg | grep -i 'Galcore version'");
				String success = result.get("success");
				if("1".equals(success)){
					//读取CPU信息
					String stdout = result.get("stdout");
					InputStreamReader ir = new InputStreamReader(new BufferedInputStream(new ByteArrayInputStream(stdout.getBytes(StandardCharsets.UTF_8))));
					LineNumberReader input = new LineNumberReader(ir);
					//查找CPU序列号
					for (int i = 1; i < 100; i++) {
						str = input.readLine();
						if (str != null) {
							//查找到序列号所在行
							if (str.indexOf(" version ") > -1) {
								//提取序列号
								strNPU = str.substring(str.indexOf(":") + 9,
										str.length());
								//去空格
								npuVersion = strNPU.trim();
								break;
							}
						} else {
							//文件结尾
							break;
						}
					}
					input.close();
				}

				if(!"0000000000000000".equals(npuVersion)){
					BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
					bufferedWriter.write(npuVersion);
					bufferedWriter.flush();
					bufferedWriter.close();
				}
			}

		}
		catch (Exception ex){
			ex.printStackTrace();
		}
		return npuVersion;
	}

	public static String getMacAddress() {
		return MacHelper.getMacAddress(ContextHolder.getInstance().getContext());
	}

	public static String getIpAddress(Context context){
		String hostIp = null;
		try {
			Enumeration nis = NetworkInterface.getNetworkInterfaces();
			InetAddress ia = null;
			while (nis.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) nis.nextElement();
				Enumeration<InetAddress> ias = ni.getInetAddresses();
				while (ias.hasMoreElements()) {
					ia = ias.nextElement();
					if (ia instanceof Inet6Address) {
						continue;// skip ipv6
					}
					String ip = ia.getHostAddress();
					if (!"127.0.0.1".equals(ip)) {
						hostIp = ia.getHostAddress();
						break;
					}
				}
			}
		} catch (SocketException e) {
			NeuLogUtils.eTag(TAG,e.getMessage(),e);
		}
		return hostIp;
	}

	private static String intToIp(int i)  {
		return (i & 0xFF)+ "." + ((i >> 8 ) & 0xFF) + "." + ((i >> 16 ) & 0xFF) +"."+((i >> 24 ) & 0xFF );
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	public static CpuUsageInfo getCpuRate(){
		return android.os.CpuUsageInfo.CREATOR.createFromParcel(Parcel.obtain());
	}

	public static DiskInfo readSystem() {
		File root = Environment.getRootDirectory();
		StatFs sf = new StatFs(root.getPath());
		long blockSize = sf.getBlockSizeLong();
		long blockCount = sf.getBlockCountLong();
		long availCount = sf.getAvailableBlocksLong();
		DiskInfo diskInfo = new DiskInfo();
		diskInfo.setTotal(blockSize*blockCount/1024/1024);
		diskInfo.setUsed(blockSize*blockCount/1024/1024-availCount*blockSize/1024/1024);

		return diskInfo;
	}
	public static SDInfo readSD() {
		int type = getStoreType();
		if(SDCARD_TYPE!=type){
			return null;
		}
		File root = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(root.getPath());
		long blockSize = sf.getBlockSizeLong();
		long blockCount = sf.getBlockCountLong();
		long availCount = sf.getAvailableBlocksLong();
		SDInfo sdInfo = new SDInfo();
		sdInfo.setTotal(blockSize*blockCount/1024/1024);
		sdInfo.setUsed(blockSize*blockCount/1024/1024-availCount*blockSize/1024/1024);

		return sdInfo;
	}

	public static boolean isSDCardExit(){
		return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}

	public static String getSystemProperties(String key, String def) {
		try {
			Method systemProperties_get = Class.forName("android.os.SystemProperties").getMethod("get", String.class);
			String ret = (String) systemProperties_get.invoke(null, key);
			NeuLogUtils.dTag(TAG, key + "= " + ret);
			if (ret != null && !StrUtil.isEmpty(ret)){
				return ret;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return def;
	}

	public static String getSystemPropertiesCrop(String key, String def) {
		try {
			Method systemProperties_get = Class.forName("android.os.SystemProperties").getMethod("get", String.class);
			String ret = (String) systemProperties_get.invoke(null, key);
			NeuLogUtils.dTag(TAG, key + "= " + ret);
			if (ret != null && !StrUtil.isEmpty(ret)){
				Log.e(TAG, "---------->>>>>>>> 当前系统是最新版本：getprop ro.product.build.dim  未裁剪过 原始的  =  " + ret);

				String value = null;
				String strVer = ret;
				int end = strVer.length();
				if (strVer.contains("V")) {
					int index = strVer.lastIndexOf("V");
					value = strVer.substring(index, end);
				} else if (strVer.contains("v")) {
					int index = strVer.lastIndexOf("v");
					value = strVer.substring(index, end);
				}
				Log.e(TAG, "---------->>>>>>>> 当前系统是最新版本：getprop ro.product.build.dim  裁剪过的  =  " + value);

				return value;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return def;
	}

	public static String getSkuToken(){
		String line = null;
		File config = new File(getNeucore(ContextHolder.getInstance().getContext())+File.separator+"license.conf");
		if(config.exists()){
			BufferedReader bufferedReader = null;
			try {
				int index = 0;
				bufferedReader = new BufferedReader(new FileReader(config));
				while (index < 2) {
					index++;
					line = bufferedReader.readLine();
					if(index==2){
						int idx = -1;
						if((idx=line.indexOf("="))!=-1){
							line = line.substring(idx+1);
						}
						break;
					}
				}
			}
			catch (Exception ex){
				ex.printStackTrace();
			}
			finally {
				if(bufferedReader!=null){
					try {
						bufferedReader.close();
					} catch (IOException e) {
					}
				}
			}
		}
		NeuLogUtils.dTag(TAG, "SkuToken="+line);
		return line;
	}
}
