package com.neucore.neulink.util;


import android.content.Context;
import android.util.Base64;

import com.neucore.neulink.impl.GzipRequestInterceptor;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.log.NeuLogUtils;
import com.google.gson.annotations.SerializedName;
import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.NeulinkConst;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.util.ObjectUtil;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NeuHttpHelper implements NeulinkConst{

	private static String TAG = TAG_PREFIX+"NeuHttpHelper";

	private static OkHttpClient getClient(){
		return getClient(false,5,5);
	}

	private static OkHttpClient getClient(boolean debug,int connTimeout,int readTimeout){
		Boolean isCompress = ConfigContext.getInstance().getConfig(ConfigContext.PRODUCT_COMPRESS,true);
		if(debug||!isCompress){
			OkHttpClient okHttpClient = new OkHttpClient.Builder()
					.connectTimeout(connTimeout, TimeUnit.MINUTES)//设置连接超时时间
					.readTimeout(readTimeout, TimeUnit.MINUTES)//设置读取超时时间
					.writeTimeout(readTimeout,TimeUnit.MINUTES)
					.sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), SSLSocketClient.trustManager)
					.hostnameVerifier(SSLSocketClient.getHostnameVerifier()) //支持HTTPS请求，跳过证书验证
					.build();
			return okHttpClient;
		}

		OkHttpClient okHttpClient = new OkHttpClient.Builder()
				.addInterceptor(new GzipRequestInterceptor())
				.connectTimeout(connTimeout, TimeUnit.MINUTES)//设置连接超时时间
				.readTimeout(readTimeout, TimeUnit.MINUTES)//设置读取超时时间
				.writeTimeout(readTimeout,TimeUnit.MINUTES)
				.sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), SSLSocketClient.trustManager)
				.hostnameVerifier(SSLSocketClient.getHostnameVerifier()) //支持HTTPS请求，跳过证书验证
				.build();
		return okHttpClient;
	}

	private static Request createRequest(String fileUrl){
		//第二步构建Request对象
		okhttp3.Request request = new Request.Builder()
				.url(fileUrl)
				.get()
				.build();
		return request;
	}

	public static String getFileName(String fileUrl){
		if(fileUrl==null||fileUrl.trim().length()==0){
			return "";
		}
		if(fileUrl.lastIndexOf("/")==-1){
			return fileUrl;
		}
		String fileName = fileUrl.substring(fileUrl.lastIndexOf("/")+1);
		return fileName;
	}

	/**
	 * 下载json文件
	 * @param fileUrl
	 * @return
	 * @throws IOException
	 */
	public static String dldFile2String(String fileUrl, int tryNum) throws NeulinkException {

		int trys = 1;
		while(trys<=tryNum){
			try {
				Response response = getClient().newCall(createRequest(fileUrl)).execute();
				int code = response.code();
				if(code!=200){
					throw new NeulinkException(code,fileUrl+",下载失败 with code="+code);
				}
				return response.body().string();
			}
			catch (IOException ex){
				NeuLogUtils.eTag(TAG,"第"+trys+"次下载"+fileUrl+"文件失败：",ex);
				if(trys==tryNum) {
					throw new NeulinkException(NeulinkException.CODE_50001,NeulinkException.CODE_50001_MESSAGE,ex);
				}
				trys++;
				continue;
			}
			catch (RuntimeException ex){
				throw new NeulinkException(NeulinkException.CODE_50001,NeulinkException.CODE_50001_MESSAGE,ex);
			}
		}
		return null;
	}

	/**
	 *
	 * @param context
	 * @param reqId
	 * @param fileUrl
	 * @return context.getFilesDir()/dlddir/reqId/fileName
	 * @throws IOException
	 */
	public static File dld2File(Context context, String reqId, String fileUrl) throws IOException {
		String toDirStr = DeviceUtils.getTmpPath(context);
		File toDir = new File(toDirStr);
		toDir.mkdirs();
		return dld2File(context, reqId, fileUrl,toDir);
	}

	/**
	 *
	 * @param context
	 * @param reqId
	 * @param fileUrl
	 * @param toDir
	 * @return toDir/reqId/fileName
	 * @throws IOException
	 */
	public static File dld2File(Context context, String reqId,String fileUrl, File toDir) throws IOException {
		return dld2File(context, reqId, fileUrl, toDir,10,60);
	}

	/**
	 *
	 * @param context
	 * @param reqId
	 * @param fileUrl
	 * @param toDir
	 * @param connTime
	 * @param execTime
	 * @return toDir/reqId_+fileName
	 * @throws IOException
	 */
	public static File dld2File(Context context, String reqId,String fileUrl, File toDir,int connTime,int execTime) throws IOException {
		return dld2File(context,reqId,fileUrl,toDir,connTime,execTime,3);
	}

	/**
	 * 下载远程文件
	 * @param fileUrl
	 * @param toDir 为空时，自动下载到DeviceUtils.getTmpPath（）；
	 * @return File ftoDir/reqId_fileName
	 * @throws IOException
	 */
	public static File dld2File(Context context, String reqId,String fileUrl, File toDir,int connTime,int execTime,int tryNum) throws IOException {

		File tmpFile = null;
		InputStream is = null;
		FileOutputStream outStream = null;

		if(toDir==null){
			toDir = new File(DeviceUtils.getTmpPath(context));
		}
		String pathStr = toDir+File.separator+reqId+File.separator;
		File path = new File(pathStr);
		path.mkdirs();
		String fileName = fileUrl.substring(fileUrl.lastIndexOf("/")+1);
		int index = fileName.indexOf(".");
		if(index!=-1){
			String prefix = "neutemp-";
			String suffix = fileName.substring(index);
			tmpFile = File.createTempFile(prefix,suffix,path);
			tmpFile.deleteOnExit();
		}
		NeuLogUtils.dTag(TAG,"本地文件名："+tmpFile.getAbsolutePath());

		Response response = null;
		int trys = 1;

		OkHttpClient client = getClient(false,connTime,execTime);
		int code = 200;
		while(trys<=tryNum){
			try{
				response = client.newCall(createRequest(fileUrl)).execute();
				code = response.code();
				if(code!=200){
					throw new NeulinkException(code,fileUrl+",下载失败 with code="+code);
				}
				is = response.body().byteStream();

				outStream = new FileOutputStream(tmpFile);

				byte[] buffer = new byte[1024];
				int readed = 0;
				while ((readed = is.read(buffer)) != -1) {
					outStream.write(buffer, 0, readed);
				}
				break;
			}
			catch (IOException ex){
				NeuLogUtils.eTag(TAG,"第"+trys+"次下载"+fileUrl+"文件失败：",ex);
				if(trys==tryNum) {
					throw new NeulinkException(NeulinkException.CODE_50001,NeulinkException.CODE_50001_MESSAGE,ex);
				}
				trys++;
				continue;
			}
			catch (NeulinkException ex){
				throw ex;
			}
			catch (RuntimeException ex){
				NeuLogUtils.eTag(TAG,"第"+trys+"下载"+fileUrl+"文件失败：",ex);
				throw new NeulinkException(NeulinkException.CODE_50001,NeulinkException.CODE_50001_MESSAGE,ex);
			}
			finally {
				try {
					if (is != null) {
						is.close();
					}
					if (outStream != null) {
						outStream.close();
					}
				}
				catch (IOException ex){}
			}
		}

		return tmpFile;
	}

	public static String post(String url,String json,int connTime,int execTime,int tryNum){

		Response response = null;
		int trys = 1;
		int code = 200;
		InputStream is = null;

		MediaType JSON = MediaType.parse("application/json; charset=utf-8");
		OkHttpClient client = getClient(false,connTime,execTime);
		RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
		Request request = new Request.Builder()
				.url(url)
				.post(requestBody)
				.build();
		while(trys<=tryNum){
			try {
				response = client.newCall(request).execute();
				code = response.code();
				if (code != 200) {
					throw new NeulinkException(code,url + ",失败 with code=" + code);
				}
				String responseData = response.body().string();
				return responseData;
			}
			catch (IOException ex){
				NeuLogUtils.eTag(TAG,"第"+trys+"下载"+url+"失败：",ex);
				if(trys==tryNum) {
					throw new NeulinkException(NeulinkException.CODE_50001,NeulinkException.CODE_50001_MESSAGE,ex);
				}
				trys++;
				continue;
			}
			catch (NeulinkException ex){
				throw ex;
			}
			catch (RuntimeException ex){
				NeuLogUtils.eTag(TAG,"第"+trys+"请求"+url+"失败：",ex);
				throw new NeulinkException(NeulinkException.CODE_50001,NeulinkException.CODE_50001_MESSAGE,ex);
			}
			finally {
				try {
					if (is != null) {
						is.close();
					}
				}
				catch (IOException ex){}
			}
		}
		throw new RuntimeException(url + ",失败 with code=" + code);
	}

	public static String post(boolean debug,String url, String json, Map<String,String> headers, int connTime, int execTime, int tryNum){

		Response response = null;
		int trys = 1;
		int code = 200;
		InputStream is = null;

		MediaType JSON = MediaType.parse("application/json; charset=utf-8");

		OkHttpClient client = getClient(debug,connTime,execTime);
		RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
		Request request = null;
		if(ObjectUtil.isNotEmpty(headers)){
			Headers headers_ = Headers.of(headers);
			request = new Request.Builder()
					.url(url)
					.headers(headers_)
					.post(requestBody)
					.build();
		}
		else{
			request = new Request.Builder()
					.url(url)
					.post(requestBody)
					.build();
		}
		while(trys<=tryNum){
			try {
				response = client.newCall(request).execute();
				code = response.code();
				NeuLogUtils.iTag(TAG,code);
				if (code != 200) {
					throw new NeulinkException(code,url + ",失败 with code=" + code);
				}
				String responseData = response.body().string();
				return responseData;
			}
			catch (IOException ex){
				NeuLogUtils.eTag(TAG,"第"+trys+"请求"+url+"失败：",ex);
				if(trys==tryNum) {
					throw new NeulinkException(NeulinkException.CODE_50001,NeulinkException.CODE_50001_MESSAGE,ex);
				}
				trys++;
				continue;
			}
			catch (NeulinkException ex){
				throw ex;
			}
			catch (RuntimeException ex){
				NeuLogUtils.eTag(TAG,"第"+trys+"请求"+url+"失败：",ex);
				throw new NeulinkException(NeulinkException.CODE_50001,NeulinkException.CODE_50001_MESSAGE,ex);
			}
			finally {
				try {
					if (is != null) {
						is.close();
					}
				}
				catch (IOException ex){}
			}
		}
		throw new RuntimeException(url + ",失败 with code=" + code);
	}

	public static String post(String url, String json, Map<String,String> headers, int connTime, int execTime, int tryNum, Interceptor interceptor){
		Response response = null;
		int trys = 1;
		int code = 200;
		InputStream is = null;

		MediaType JSON = MediaType.parse("application/json; charset=utf-8");
		OkHttpClient client = getClient(false,connTime,execTime);
		RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
		Request request = null;
		if(ObjectUtil.isNotEmpty(headers)){
			Headers headers_ = Headers.of(headers);
			request = new Request.Builder()
					.url(url)
					.headers(headers_)
					.post(requestBody)
					.build();
		}
		else{
			request = new Request.Builder()
					.url(url)
					.post(requestBody)
					.build();
		}
		while(trys<=tryNum){
			try {
				response = client.newCall(request).execute();
				code = response.code();
				NeuLogUtils.iTag(TAG,code);
				if (code != 200) {
					throw new NeulinkException(code,url + ",失败 with code=" + code);
				}
				String responseData = response.body().string();
				return responseData;
			}
			catch (IOException ex){
				NeuLogUtils.eTag(TAG,"第"+trys+"请求"+url+"失败：",ex);
				if(trys==tryNum) {
					throw new NeulinkException(NeulinkException.CODE_50001,NeulinkException.CODE_50001_MESSAGE,ex);
				}
				trys++;
				continue;
			}
			catch (NeulinkException ex){
				throw ex;
			}
			catch (RuntimeException ex){
				NeuLogUtils.eTag(TAG,"第"+trys+"请求"+url+"失败：",ex);
				throw new NeulinkException(NeulinkException.CODE_50001,NeulinkException.CODE_50001_MESSAGE,ex);
			}
			finally {
				try {
					if (is != null) {
						is.close();
					}
				}
				catch (IOException ex){}
			}
		}
		throw new RuntimeException(url + ",失败 with code=" + code);
	}

	private static String NetImageToBase64(String netImagePath) throws Exception {
		InputStream is = null;
		final ByteArrayOutputStream data = new ByteArrayOutputStream();
		// 创建URL
		URL url = new URL(netImagePath);
		final byte[] by = new byte[1024];
		// 创建链接
		final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5000);
		try {
			is = conn.getInputStream();
			// 将内容读取内存中
			int len = -1;
			while ((len = is.read(by)) != -1) {
				data.write(by, 0, len);
			}
			// 对字节数组Base64编码
			String result = Base64.encodeToString(data.toByteArray(), Base64.DEFAULT);
			return result;

		} finally {
			if(is!=null){
				// 关闭流
				try {
					is.close();
				}
				catch (Exception ex){}
			}
		}
	}
	private static String getBase64(){
		return "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";
	}
}

class Face{
	@SerializedName("code")
	private int code;
	@SerializedName("msg")
	private String msg;
	@SerializedName("data")
	private String data;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
