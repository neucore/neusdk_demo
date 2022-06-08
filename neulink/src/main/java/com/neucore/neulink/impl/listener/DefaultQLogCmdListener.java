package com.neucore.neulink.impl.listener;

import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.neucore.neulink.log.LogUtils;
import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.impl.cmd.rmsg.log.DnloadCmd;
import com.neucore.neulink.impl.cmd.rmsg.log.LogActionResult;
import com.neucore.neulink.impl.NeulinkEvent;
import com.neucore.neulink.impl.StorageFactory;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.FileUtils;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.MD5Utils;
import com.neucore.neulink.util.RequestContext;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DefaultQLogCmdListener implements ICmdListener<LogActionResult, DnloadCmd> {
    private String TAG = "DefaultQLogCmdListener";

    @Override
    public LogActionResult doAction(NeulinkEvent<DnloadCmd> event) {
        try{
            DnloadCmd cmd = event.getSource();
            LogActionResult result = new LogActionResult();

            if("user".equalsIgnoreCase(cmd.getType())){
                return getUser(cmd);
            }
            else if("sys".equalsIgnoreCase(cmd.getType())){
                return getSys(cmd);
            }
            else if("app".equalsIgnoreCase(cmd.getType())){
                return getSys(cmd);
            }
            throw new RuntimeException(cmd.getType()+"日志类型不支持");
        }
        catch (Throwable ex){
            LogUtils.eTag(TAG,"process",ex);
            throw new RuntimeException(ex);
        }
    }


    private LogActionResult getUser(DnloadCmd cmd) throws IOException, NoSuchAlgorithmException, ClientException, ServiceException {

        int offset = 0;

        LogActionResult result = new LogActionResult();

        long start = 0;

        long end = 0;

        try {
            start= sdf.parse(cmd.getStart()).getTime()/1000;
            end = sdf.parse(cmd.getEnd()).getTime()/1000;
        }
        catch (Exception e){

        }

        return result;
    }

    protected File store(NeulinkTopicParser.Topic topic, String dataPath, int index, Object[] dataArray) throws IOException {
        String path = ContextHolder.getInstance().getContext().getFilesDir() + "/" + dataPath + "/" + topic.getReqId() + "/";
        new File(path).mkdirs();
        path = path + "/" + index + ".json";
        File localFile = new File(path);
        localFile.createNewFile();
        FileWriter fileWriter = new FileWriter(localFile);
        String logs = JSonUtils.toJson(dataArray);
        fileWriter.write(logs);
        fileWriter.close();
        return localFile;
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");//日志名称格式

    private LogActionResult getSys(final DnloadCmd cmd) throws IOException, NoSuchAlgorithmException, ClientException, ServiceException {

        File logs = new File(DeviceUtils.getLogPath(ContextHolder.getInstance().getContext()));

        String startStr = cmd.getStart();//yyyy-MM-dd HH
        String endStr = cmd.getEnd();//yyyy-MM-dd HH
        Date tstart = null;
        Date tend = null;
        try {
            tstart = sdf.parse(startStr);
            tend = sdf.parse(endStr);
        } catch (ParseException e) {
            throw new RuntimeException("查询条件格式不对，请按照'yyyy-MM-dd HH'方式输入",e);
        }
        final Date start = tstart;
        final Date end = tend;

        /**
         * 获取在开始时间和结束时间范围内的文件
         */
        File[] logFiles = logs.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String name = pathname.getName();
                if(name.indexOf(LOG_CARSH)==-1){
                    return false;
                }
                try {
                    Date date = sdf.parse(name);
                    if(start.getTime()<=date.getTime() && date.getTime()<=end.getTime()){
                        return true;
                    }
                } catch (ParseException e) {
                    LogUtils.eTag(TAG,"getSys",e);
                }
                return false;
            }
        });


        int offset = 0;

        LogActionResult result = new LogActionResult();

        long count = logFiles.length;
        result.setPages(count);
        List<String> urls = new ArrayList<String>();
        List<String> md5s = new ArrayList<String>();
        String reqDir = DeviceUtils.getTmpPath(ContextHolder.getInstance().getContext())+File.separator+ RequestContext.getId();
        new File(reqDir).mkdirs();

        for(int i=1;i<count+1;i++){

            File localFile = logFiles[i-1];

            String path  = reqDir+File.separator+i+".zip";

            FileUtils.zip(localFile.getPath(),path);

            String md5 = MD5Utils.getInstance().getMD5File(path);

            String url = StorageFactory.getInstance().uploadLog(path, RequestContext.getId(),(i));

            md5s.add(md5);
            urls.add(url);

        }
        FileUtils.deleteDirectory(reqDir);
        result.setOffset(1);
        result.setUrl(urls.get(0));
        result.setMd5(md5s.get(0));
        return result;
    }
}
