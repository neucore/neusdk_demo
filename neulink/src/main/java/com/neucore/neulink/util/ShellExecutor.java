package com.neucore.neulink.util;

import android.content.Context;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.neucore.neulink.NeulinkConst;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ShellExecutor implements NeulinkConst{

    private static String TAG = TAG_PREFIX+"ShellExecutor";
    /**
     *
     * @param context
     * @param cmd
     * @return Map<String,String> shellRet,stdout
     */
    public static synchronized Map<String,String> run(Context context,String[] cmd) {
        InputStream in = null;
        String shellOutDir = "/";

        Map<String,String> response = new HashMap<String,String>();
        StringBuffer result = new StringBuffer();
        try {
            // 创建操作系统进程（也可以由Runtime.exec()启动）
            // RuntimeInfo runtime = RuntimeInfo.getRuntime();
            // Process proc = runtime.exec(cmd);
            // InputStream inputstream = proc.getInputStream();
            ProcessBuilder builder = new ProcessBuilder(cmd);
            // 设置一个路径（绝对路径了就不一定需要）
            if (shellOutDir != null) {
                // 设置工作目录（同上）
                builder.directory(new File(shellOutDir));
                // 合并标准错误和标准输出
                builder.redirectErrorStream(true);

                // 启动一个新进程
                Process process = builder.start();

                // 读取进程标准输出流
                in = process.getInputStream();
                BufferedReader br=new BufferedReader(new InputStreamReader(in,"gbk"));
                String line = null;
                while ((line=br.readLine()) != null) {
                    result = result.append(line);
                    result.append(System.lineSeparator());
                }

                response.put("shellRet",String.valueOf(process.exitValue()));

                response.put("stdout",result.toString());
            }
        } catch (Exception ex) {
            LogUtils.eTag(TAG,"shell exe error",ex);
            throw new RuntimeException(ex);
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (Exception ex){
                LogUtils.eTag(TAG,"shell exe error",ex);
            }
        }
        return response;
    }

    //OTA APP升级
    public static synchronized Map<String,String> execute(Context context,String cmd) {
        Map<String,String> response = new HashMap<String,String>();
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
            StringBuilder successMsg = new StringBuilder();
            StringBuilder errorMsg = new StringBuilder();
            BufferedReader successResultBuffer = new BufferedReader(new InputStreamReader(process.getInputStream(),"GBK"));
            BufferedReader errorResultBuffer = new BufferedReader(new InputStreamReader(process.getErrorStream(),"GBK"));
            String s = null;
            while ((s = successResultBuffer.readLine()) != null) {
                successMsg.append(s);
                successMsg.append(System.lineSeparator());

            }

            while ((s = errorResultBuffer.readLine()) != null) {
                errorMsg.append(s);
                errorMsg.append(System.lineSeparator());
            }
            if(errorMsg.length()>0){
                response.put("stdout",errorMsg.toString());
                response.put("success","0");
            }
            else{
                response.put("stdout",successMsg.toString());
                response.put("success","1");
            }
            response.put("shellRet",String.valueOf(process.exitValue()));

        } catch (Exception e) {
            response.put("stdout",e.getMessage());
        }
        return response;
    }
}
