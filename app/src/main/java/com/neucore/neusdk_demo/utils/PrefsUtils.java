package com.neucore.neusdk_demo.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;

import java.util.Set;

@SuppressLint("NewApi")
public class PrefsUtils {
	private static String TAG = "PrefsUtils";
	private static final String SHARED_TXY = "kaoqin";
	public static void writePrefs(Context context, String prefsName, String prefsValue) {
		try {
			SharedPreferences sharedata = context.getSharedPreferences(SHARED_TXY, Context.MODE_PRIVATE );
			SharedPreferences.Editor editor = sharedata.edit();
			editor.putString(prefsName, prefsValue);
			editor.commit();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	public static String readPrefs(Context context, String prefsName) {
		String prefsValue = "";
		try {
			SharedPreferences sharedata = context.getSharedPreferences(SHARED_TXY, Context.MODE_PRIVATE);
			prefsValue = sharedata.getString(prefsName, "");
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return prefsValue;
	}
	public static String readPrefs(Context context, String prefsName,String def) {
		String prefsValue = "";
		try {
			SharedPreferences sharedata = context.getSharedPreferences(SHARED_TXY, Context.MODE_PRIVATE);
			prefsValue = sharedata.getString(prefsName, def);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return prefsValue;
	}
	
	public static boolean readBooleanPrefs(Context context, String prefsName) {
		boolean prefsValue = false;
		try {
			SharedPreferences sharedata = context.getSharedPreferences(SHARED_TXY, Context.MODE_PRIVATE);
			prefsValue = sharedata.getBoolean(prefsName, true);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return prefsValue;
	}
	
	public static void writeBooleanPrefs(Context context, String prefsName, boolean prefsValue) {
		try {
			SharedPreferences sharedata = context.getSharedPreferences(SHARED_TXY, Context.MODE_PRIVATE );
			SharedPreferences.Editor editor = sharedata.edit();
			editor.putBoolean(prefsName, prefsValue);
			editor.commit();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}
	public static void writeSetPrefs(Context context, String prefsName, Set<String> prefsValue) {
		try {
			SharedPreferences sharedata = context.getSharedPreferences(SHARED_TXY, Context.MODE_PRIVATE );
			SharedPreferences.Editor editor = sharedata.edit();
			editor.putStringSet(prefsName, prefsValue);
			editor.commit();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}
	public static Set<String> readSetPrefs(Context context, String prefsName) {
		Set<String> prefsValue = null;
		try {
			SharedPreferences sharedata = context.getSharedPreferences(SHARED_TXY, Context.MODE_PRIVATE);
			prefsValue = sharedata.getStringSet(prefsName, null);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return prefsValue;
	}
	public static void writeFloatPrefs(Context context, String prefsName, float prefsValue) {
		try {
			SharedPreferences sharedata = context.getSharedPreferences(SHARED_TXY, Context.MODE_PRIVATE );
			SharedPreferences.Editor editor = sharedata.edit();
			editor.putFloat(prefsName, prefsValue);
			editor.commit();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	public static float readFloatPrefs(Context context, String prefsName) {
		float prefsValue = 0;
		try {
			SharedPreferences sharedata = context.getSharedPreferences(SHARED_TXY, Context.MODE_PRIVATE);
			prefsValue = sharedata.getFloat(prefsName, 0);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return prefsValue;
	}
	public static void checkSign(Context context) {  
	     if (!isMyApp(context)){  
	        //直接退出  
	    	 android.os.Process.killProcess(android.os.Process.myPid());    //获取PID 
	    	  System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出
	       }  
	   }  
	private static boolean isMyApp(Context context){  
	     String signStr=getSign(context);  
	     return "308202bd308201a5a00302010202043c78d584300d06092a864886f70d01010b0500300e310c300a06035504031303756d693020170d3137303332393032353235315a180f33303136303733303032353235315a300e310c300a06035504031303756d6930820122300d06092a864886f70d01010105000382010f003082010a0282010100b9acbd981d1aaac77b3703a33274ca57317f7ee44175aa1f7574b8c726af0d05d7d6e6d380ce2852279c80167aca9a1c72ce87905164d99a080277b00e6e9aa209bf845741db2bf71d60a9fcca516e60c68498dd46f344ba4396da04e507bb1d5a8138db42c7a49d8fbf74ccbc1e05a1a054005486412534b59298ed0445e0d4fa80600a2ee3c572f3d46f60c6bf03529895b4c32bcee42ca82faefef45355d5d809c3af1db893298331e49e20067a9d04ca89f2bbbbd32e301d416871a345f409d8b0755b1b00e9ac17bc3781b7e1d62d1543c442a4238a82236c9ba109ac8b3ca2c0e38d4c52aab656f564b77968886792e2f7a9ec34e651dec5ff09d0f7650203010001a321301f301d0603551d0e041604146ae60e6d49f23b9a8297334b746783ec3e5f9f4c300d06092a864886f70d01010b050003820101004871a26712c3139c24c1df3b9e729fca120fa881f0f528d1b34a909159ff373fae4102bd12d20d8650436dfcec42fc37dcc2b6c9391e4ffc2e1d02a3e768737607380fb7c3da75a61348f077e9b0b6605332488a8292b9fced07b323326f2350cab56afe50d47b81ad59dc143360de5121b5a15d3c08fd7c104e7c4ac09d347215676bc467debfa52048405d02e145a6cab7b461334d45965473fb81e98eeafc50cb75f6d2e7cc3451def5923cddf38da22aaf9e04430753c32989ec95fd260fca54d5fa67d9766983925049176eca12080be87aef1d270e4c14fc20820e6fe045a607adee1475ab47b74025f6d4b27732bf2812ea0eeb0ad8d33eb6675e99b7".equals(signStr);  
	   }  
	private static String getSign(Context context){  
	   try {  
	       PackageInfo packageInfo=context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);  
	       Signature[] signatures=packageInfo.signatures;  
	       StringBuilder builder=new StringBuilder();  
	       for (Signature signature:signatures){  
	           builder.append(signature.toCharsString());  
	       }  
	       return builder.toString();  
	   } catch (PackageManager.NameNotFoundException e) {
		   Log.e(TAG, e.getMessage(), e);
	   }  
	   return "";  
	  }
//	public static DaHua readDaHuaPrefs(Context context, String key) {
//		SharedPreferences sharedPreferences=context.getSharedPreferences(SHARED_TXY,context.MODE_PRIVATE);
//		String temp = sharedPreferences.getString(key, "");
//		ByteArrayInputStream bais =  new ByteArrayInputStream(Base64.decode(temp));
//		DaHua user = null;
//		try {
//			ObjectInputStream ois = new ObjectInputStream(bais);
//			user = (DaHua) ois.readObject();
//		} catch (IOException e) {
//		}catch(ClassNotFoundException e1) {
//
//		}
//		return user;
//	}
}
