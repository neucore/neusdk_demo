package com.neucore.neusdk_demo.neulink.extend;

import com.neucore.neulink.NeulinkException;
import com.neucore.neulink.cmd.bak.BackupItem;
import com.neucore.neulink.cmd.cfg.ConfigContext;
import com.neucore.neulink.extend.ICmdListener;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.extend.QueryResult;
import com.neucore.neulink.extend.StorageFactory;
import com.neucore.neulink.impl.NeuLinkConstant;
import com.neucore.neulink.cmd.recv.RecoverCmd;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.MD5Utils;
import com.neucore.neulink.util.NeuHttpHelper;
import com.neucore.neulink.util.RequestContext;
import com.neucore.neusdk_demo.db.DaoManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BackupActionListener implements ICmdListener<QueryResult>, NeuLinkConstant {
    @Override
    public QueryResult doAction(NeulinkEvent event) {
        /**
         * 最新下载的算法文件
         */
        RecoverCmd cmd = (RecoverCmd)event.getSource();

        String url = cmd.getUrl();
        String backUrl = null;
        try {
            String json  = NeuHttpHelper.dldFile2String(url,3);
            BackupItem[] items = (BackupItem[]) JSonUtils.toObject(json, BackupItem[].class);
            /**
             * 备份打包@TODO
             */
            backUrl = doBackup(items);

        } catch (IOException e) {
            throw new NeulinkException(500,e.getMessage());
        }

        QueryResult result = new QueryResult();
        /**
         * 200表示成功 500：表示错误
         */
        result.setCode(200);
        /**
         * 错误信息
         */
        result.setMessage("success");
        Map<String,String> datas = new HashMap<String,String>();
        datas.put("url",backUrl);
        result.setData(datas);
        return result;
    }

    /**
     * 备份描述文件【json】的url
     * @param backups
     * @return
     */
    private String doBackup(BackupItem[] backups){
        int len = backups==null?0:backups.length;
        HashMap<String,BackupItem> urls = new HashMap<String,BackupItem>();
        for(int i=0;i<len;i++){
            String path = null;

            if(Backup_Obj_Cfg.equalsIgnoreCase(backups[i].getObj())){//应用配置备份
                path= cfgBackup();
            }
            else if(Backup_Obj_Syscfg.equalsIgnoreCase(backups[i].getObj())){//系统配置备份
                path = syscfgBackup();
            }
            if(Backup_Obj_Data.equalsIgnoreCase(backups[i].getObj())){//数据库数据
                path = dataBackup();
            }
            if(path!=null){
                BackupItem item = new BackupItem();
                item.setObj(backups[i].getObj());
                String url = StorageFactory.getInstance().uploadBak(path, RequestContext.getId(),1);
                item.setUrl(url);
                try {
                    item.setMd5(MD5Utils.getInstance().getMD5File(path));
                    urls.put(backups[i].getObj(),item);
                } catch (Exception e) {

                }
            }
        }
        String bakUrl = null;
        if(urls.size()>0){
            String tmpFile = backJson(urls);
            bakUrl = StorageFactory.getInstance().uploadBak(tmpFile,RequestContext.getId(),1);
        }

        return bakUrl;
    }

    private String backJson(HashMap<String,BackupItem> urls){
        String tmpDir = DeviceUtils.getTmpPath(ContextHolder.getInstance().getContext())+"/bak.json";
        int len = urls == null?0:urls.size();
        if(len>0){
            BackupItem[] backups = new BackupItem[len];
            urls.values().toArray(backups);
            String json = JSonUtils.toJson(backups);
            FileWriter fw = null;
            try {
                fw = new FileWriter(new File(tmpDir));
                fw.write(json);
                return tmpDir;
            } catch (IOException e) {
            }
            finally {
                if(fw!=null){
                    try {
                        fw.close();
                    } catch (IOException e) {
                    }
                }
            }

        }
        return null;
    }

    private String cfgBackup(){
        return ConfigContext.getInstance().getConfigFile();
    }

    private String syscfgBackup(){
        return null;
    }

    private String dataBackup(){

        String dirPath = DeviceUtils.getDBPath(ContextHolder.getInstance().getContext());

        String path=null;
        File parentFile=new File(dirPath);
        if(!parentFile.exists()){
            parentFile.mkdirs();
        }
        String parentPath=parentFile.getAbsolutePath();
        if(parentPath.lastIndexOf("\\/")!=-1){
            path=dirPath + DaoManager.DB_NAME;
        }else{
            path=dirPath+File.separator+DaoManager.DB_NAME;
        }

        return path;
    }
}
