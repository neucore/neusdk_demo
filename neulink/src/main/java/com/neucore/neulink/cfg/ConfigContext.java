package com.neucore.neulink.cfg;

import android.content.Context;
import android.util.Log;

import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.EnDecUtil;
import com.neucore.neulink.util.FileUtils;
import com.neucore.neulink.util.NeuHttpHelper;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;
import java.util.Properties;

public class ConfigContext {

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
    public final static String LOG_LEVEL = "Log.Level";

    public final static String  UPLOAD_CHANNEL = "upload.channel";
    public final static String REGIST_SERVER = "regist.server";

    private String TAG = "ConfigContext";

    private static ConfigContext configContext = new ConfigContext();

    private Properties defaultConfig = new Properties();
    {
        defaultConfig.setProperty(MQTT_SERVER,"WJ8vFilazdPSHmI2C0g5kl5yIDpvMYj0odLucUl/T1Y=");
        defaultConfig.setProperty(STORAGE_TYPE,"XBtKHrBKP7c=");//""NvfwywVaqXA=");
        defaultConfig.setProperty(OSS_END_POINT,"YcCiqgrFpSdKjx6yQAEi+3VOA1kk3RHA6AQ/Tx0Z9bX1iQfGu8HWFw==");
        defaultConfig.setProperty(OSS_BUCKET_NAME,"t+WPBP3ht8HWz5r7xER2+Q==");
        defaultConfig.setProperty(OSS_ACCESS_KEY_ID,"EC16crIrduFt0x1DBK7HGN27bGR/PEyF/J8JoADcSpE=");
        defaultConfig.setProperty(OSS_ACCESS_KEY_SECRET,"gGjc6oNIDIXeJq2aGNKW1pgSgCARrAU++1kx2PoW0GA=");
        defaultConfig.setProperty(FTP_SERVER,"G6y37QO742CUqJaZIgubH3R6/n1CT1EF");
        defaultConfig.setProperty(FTP_BUCKET_NAME,"1uAWhm9/bnU=");
        defaultConfig.setProperty(FTP_USER_NAME,"xT6suMoLn1w=");
        defaultConfig.setProperty(FTP_PASSWORD,"p7sQSFKvWKw=");
        defaultConfig.setProperty(TOPIC_PARTITION,"HQ+EDnzpP4k=");
        defaultConfig.setProperty(LOG_LEVEL,"ePrfOnatyOs=");
        defaultConfig.setProperty(UPLOAD_CHANNEL,"/MDody44KHg=");//0:mqtt;1:https【默认为mqtt】
        defaultConfig.setProperty(REGIST_SERVER,"YcCiqgrFpSewdKCbNPFxRoSsPNIM1pEe");
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
            }catch(Exception ex){}
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
            Properties properties = new Properties();
            reader = new FileReader(configFile);
            properties.load(reader);
            configs = properties;
        } catch (IOException e) {
            Log.e(TAG,"load",e);
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
            throw new NeulinkException(500,e.getMessage());
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

    private String getDefault(String key,String defaultValue){
        if(defaultValue!=null){
            return defaultValue;
        }
        String setting = defaultConfig.getProperty(key);
        byte[] encrypt = new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            encrypt = Base64.getDecoder().decode(setting);
        }
        setting = new String(EnDecUtil.DESDecrypt("neucore-security-key",encrypt));
        return setting;
    }
}
