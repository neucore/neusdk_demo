package com.neucore.neulink.app;

import android.content.Context;
import android.os.Environment;

import com.blankj.utilcode.util.LogUtils;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;

public class CarshHandler implements UncaughtExceptionHandler, NeulinkConst {
	private String TAG = TAG_PREFIX+"CarshHandler";
	private static CarshHandler mCrashHandler;
	private UncaughtExceptionHandler mDefaultHandler;
	Context context;
	public CarshHandler() {

	}

	public static CarshHandler getIntance() {
		if (mCrashHandler == null) {
			mCrashHandler = new CarshHandler();
		}
		return mCrashHandler;

	}

	public void init() {
		this.context= ContextHolder.getInstance().getContext();
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		}
	}

	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		// 保存日志文件
		saveCrashInfo2File(ex);

		return true;
	}

	private String saveCrashInfo2File(Throwable ex) {
		try {

			StringBuffer sb = new StringBuffer();
			Writer writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			ex.printStackTrace(printWriter);
			Throwable cause = ex.getCause();
			while (cause != null) {
				cause.printStackTrace(printWriter);
				cause = cause.getCause();
			}
			printWriter.close();
			String result = writer.toString();
			sb.append(result);
			LogUtils.eTag("error"+result);
			String path = DeviceUtils.getLogPath(context) +File.separator+ new SimpleDateFormat("yyyyMMddHHmmssSSS").format(System.currentTimeMillis()) + LOG_CARSH;
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				File dir = new File(path);
				if (!dir.getParentFile().exists()) {
					dir.getParentFile().mkdirs();
				}
				new File(path).createNewFile();
				FileOutputStream fos = new FileOutputStream(path);
				fos.write(sb.toString().getBytes());
				fos.close();
			}
			return path;
		} catch (Exception e) {
			LogUtils.eTag(TAG, e.getMessage(), e);
		}
		return null;
	}

	public File[] getFiles(){
		File file = new File(DeviceUtils.getLogPath(context));
		return file.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				String name = pathname.getName();
				if(name.indexOf(LOG_CARSH)!=-1){
					return true;
				}
				return false;
			}
		});
	}
}
