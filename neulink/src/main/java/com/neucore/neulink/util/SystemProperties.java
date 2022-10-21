package com.neucore.neulink.util;

import com.neucore.neulink.log.NeuLogUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hutool.core.util.ObjectUtil;

public class SystemProperties {
    private static String GETPROP_EXECUTABLE_PATH = "/system/bin/getprop";
    private static String SETPROP_EXECUTABLE_PATH = "/system/bin/setprop";
    private static String TAG = "SystemProperties";
    public static Properties get() {
        Process process = null;
        BufferedReader bufferedReader = null;
        Properties properties = new Properties();
        try {
            process = new ProcessBuilder().command(GETPROP_EXECUTABLE_PATH).redirectErrorStream(true).start();
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = bufferedReader.readLine();
            while (ObjectUtil.isNotEmpty(line)){
                line = line.substring(1);
                int len = line.length();
                line = line.substring(0,len-1);
                String[] prop = line.split("\\]: \\[");
                len = prop.length;
                String value = "";
                String key = prop[0];
                if(len>1){
                    value = prop[1];
                }
                properties.setProperty(key,value);

                line = bufferedReader.readLine();
            }
            return properties;
        } catch (Exception e) {
            NeuLogUtils.eTag(TAG,"Failed to read System Property ",e);
            return null;
        } finally{
            if (bufferedReader != null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {}
            }
            if (process != null){
                process.destroy();
            }
        }
    }
    public static void set(String key,String value) {
        Process process = null;
        BufferedReader bufferedReader = null;
        try {
            if(ObjectUtil.isEmpty(value)){
                value = "";
            }
            process = new ProcessBuilder().command(SETPROP_EXECUTABLE_PATH,key,value).redirectErrorStream(true).start();
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = bufferedReader.readLine();
        }
        catch (Exception e){
            NeuLogUtils.eTag(TAG,"Failed to set System Property ",e);
        }
        finally{
            if (bufferedReader != null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {}
            }
            if (process != null){
                process.destroy();
            }
        }
    }
}
