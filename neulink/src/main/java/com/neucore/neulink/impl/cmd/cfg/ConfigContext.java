package com.neucore.neulink.impl.cmd.cfg;

import android.content.Context;
import android.util.Log;

import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.FileUtils;
import com.neucore.neulink.util.NeuHttpHelper;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

public class ConfigContext implements NeulinkConst{

    private static ConfigContext configContext = new ConfigContext();

    public final static String STORAGE_OSS = "OSS";

    /**
     * 过期
     * @deprecated
     */
    public final static String STORAGE_FTP = "FTP";

    public final static String STORAGE_MYFTP = "MyFTP";

    public final static String STORAGE_AWS = "AWS";

    public final static String MQTT_SERVER = "MQTT-Server";
    public final static String STORAGE_TYPE = "Storage.Type";
    public final static String OSS_END_POINT = "OSS.EndPoint";
    public final static String OSS_BUCKET_NAME = "OSS.BucketName";
    public final static String OSS_ACCESS_KEY_ID = "OSS.AccessKeyID";
    public final static String OSS_ACCESS_KEY_SECRET = "OSS.AccessKeySecret";
    public final static String FTP_SERVER = "FTP.Server";
    public final static String FTP_BUCKET_NAME = "FTP.BucketName";
    public final static String FTP_USER_NAME = "FTP.UserName";
    public final static String FTP_PASSWORD = "FTP.Password";
    public final static String TOPIC_PARTITION = "Topic.Partition";
    public final static String LOG_LEVEL = "NeuLogUtils.Level";

    public final static String CONN_TIME_OUT = "connectTimeOut";

    public final static String READ_TIME_OUT = "readTimeOut";

    public final static String  UPLOAD_CHANNEL = "upload.channel";//0:mqtt;1:https【默认为mqtt】

    public final static String  MQTT_CHANNEL = "0";

    public final static String HTTP_CHANNEL = "1";

    public final static String STATUS_MANUAL_REPORT = "status.manual.report";

    public final static String USERNAME = "login.username";

    public final static String PASSWORD = "login.password";

    public final static String KEEP_ALIVE_INTERVAL= "keepAliveInterval";

    public final static String CONNECT_TIMEOUT= "connectTimeout";

    public final static String EXECUTOR_SERVICE_TIMEOUT= "executorServiceTimeout";

    public final static String CLEAN_SESSION = "cleanSession";

    public final static String AUTO_RECONNECT = "autoReconnect";

    public final static String MAX_RECONNECT_DELAY= "maxReconnectDelay";

    public final static String REGIST_SERVER = "regist.server";

    public final static String DEVICE_TYPE = "device.type";

    public final static String HTTP_SESSION_TIMEOUT = "http.session.timeout";

    public final static String SCOPEID = "ScopeId";

    private String TAG = TAG_PREFIX+"ConfigContext";

    public final static String MQTT_QOS = "mqtt.qos";

    public final static String MQTT_RETAINED = "mqtt.retained";

    public final static String TOPIC_MODE = "deploy.mode";

    public final static String TOPIC_LONG = "topic.long";

    public final static String TOPIC_SHORT = "topic.short";

    private Properties defaultConfig = new Properties();
    void loadDefault() {
    }

    private Properties extConfig = new Properties();
    private Properties configs = new Properties();
    private Context context = null;

    private String configFile = null;
    private ConfigContext(){
        configFile = DeviceUtils.getConfigPath(ContextHolder.getInstance().getContext());
        File tmp = new File(configFile);
        tmp.mkdirs();
        configFile = configFile+"/neuconfig.properties";
        tmp = new File(configFile);
        if(!tmp.exists()){
            try {
                tmp.createNewFile();
            }catch(Exception ex){
                NeuLogUtils.eTag(TAG,"init ConfigContext",ex);
            }
        }
        load();
    }

    public String getConfigFile(){
        return configFile;
    }

    public static ConfigContext getInstance(){
        return configContext;
    }

    private void load(){

        FileReader reader = null;
        try {
            loadDefault();
            Properties properties = new Properties();
            reader = new FileReader(configFile);
            properties.load(reader);
            Iterator<String> keys = defaultConfig.stringPropertyNames().iterator();
            while (keys.hasNext()){
                String key = keys.next();
                if(!properties.containsKey(key)){
                    properties.setProperty(key,getConfig(key));
                }
            }
            configs = properties;
        } catch (IOException e) {
            NeuLogUtils.eTag(TAG,"load",e);
        }
        finally {
            if(reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }

    }

    public void store(String request_id, String url){
        try {
            File tmpFile = NeuHttpHelper.dld2File(context,request_id,url);
            FileUtils.move(tmpFile,new File(configFile));
            tmpFile.delete();
        } catch (IOException e) {
            throw new NeulinkException(STATUS_500,e.getMessage());
        }
    }

    public void store(){
        FileWriter writer = null;
        try {
            writer = new FileWriter(configFile);
            configs.store(writer,"change by system");
        }
        catch (Exception e){}
        finally {
            if(writer!=null){
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 设置扩展配置
     * @param extConfig
     */
    public void setExtConfig(Properties extConfig){
        if(extConfig!=null){
            this.extConfig = extConfig;
        }
    }

    public synchronized void add(CfgItem[] items){
        int len=items==null?0:items.length;
        for(int i=0;i<len;i++){
            configs.setProperty(items[i].getKeyName(),items[i].getValue());
        }
        if(len>0){
            store();
        }
    }

    public synchronized void update(CfgItem[] items){
        int len=items==null?0:items.length;
        for(int i=0;i<len;i++){
            configs.setProperty(items[i].getKeyName(),items[i].getValue());
        }
        if(len>0){
            store();
        }
    }

    public synchronized void delete(CfgItem[] items){
        int len=items==null?0:items.length;
        for(int i=0;i<len;i++){
            configs.remove(items[i].getKeyName());
        }
        if(len>0){
            store();
        }
    }

    public synchronized void sync(CfgItem[] items){
        int len=items==null?0:items.length;
        Properties properties = new Properties();
        for(int i=0;i<len;i++){
            properties.setProperty(items[i].getKeyName(),items[i].getValue());
        }
        configs = properties;
        store();
    }
    public Properties getConfigs(){
        return configs;
    }
    public String getConfig(String key){
        return extConfig.getProperty(key,configs.getProperty(key,getDefault(key,null)));
    }

    public String getConfig(String key, String defaultValue){
        return extConfig.getProperty(key,configs.getProperty(key,getDefault(key,defaultValue)));
    }

    public int getConfig(String key, int defaultValue){
        return Integer.valueOf(extConfig.getProperty(key,configs.getProperty(key,getDefault(key,String.valueOf(defaultValue)))));
    }

    public long getConfig(String key, long defaultValue){
        return Long.valueOf(extConfig.getProperty(key,configs.getProperty(key,getDefault(key,String.valueOf(defaultValue)))));
    }

    public double getConfig(String key, double defaultValue){
        return Long.valueOf(extConfig.getProperty(key,configs.getProperty(key,getDefault(key,String.valueOf(defaultValue)))));
    }

    public Boolean getConfig(String key, boolean defaultValue){
        return Boolean.valueOf(extConfig.getProperty(key,configs.getProperty(key,getDefault(key,String.valueOf(defaultValue)))));
    }

    private String getDefault(String key,String defaultValue){
        if(defaultValue!=null){
            return defaultValue;
        }
        String setting = defaultConfig.getProperty(key);

        return setting;
    }
}
