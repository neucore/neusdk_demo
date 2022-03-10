package com.neucore.neulink.impl.proc;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.extend.ServiceFactory;
import com.neucore.neulink.extend.StorageFactory;
import com.neucore.neulink.impl.GProcessor;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.cmd.rmsg.log.DnloadCmd;
import com.neucore.neulink.cmd.rmsg.log.DnloadRes;
import com.neucore.neulink.cmd.rmsg.log.LogResult;
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

public class QLogProcessor extends GProcessor<DnloadCmd, DnloadRes,LogResult> {

    public QLogProcessor(Context context){
        super(context);

    }

    @Override
    public LogResult process(NeulinkTopicParser.Topic topic, DnloadCmd cmd) {
        try{
            LogResult result = new LogResult();

            if("user".equalsIgnoreCase(cmd.getType())){
                return getUser(topic,cmd);
            }
            else if("sys".equalsIgnoreCase(cmd.getType())){
                return getSys(topic,cmd);
            }
            else if("app".equalsIgnoreCase(cmd.getType())){
                return getSys(topic,cmd);
            }
            throw new RuntimeException(cmd.getType()+"日志类型不支持");
        }
        catch (Throwable ex){
            Log.e(TAG,"process",ex);
            throw new RuntimeException(ex);
        }
    }

    private LogResult getUser(NeulinkTopicParser.Topic topic, DnloadCmd cmd) throws IOException, NoSuchAlgorithmException, ClientException, ServiceException {

        int offset = 0;

        LogResult result = new LogResult();

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

    private LogResult getSys(NeulinkTopicParser.Topic topic,final DnloadCmd cmd) throws IOException, NoSuchAlgorithmException, ClientException, ServiceException {

        File logs = new File(DeviceUtils.getLogPath(this.getContext()));

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
                if(name.indexOf(NeulinkConst.LOG_CARSH)==-1){
                    return false;
                }
                try {
                    Date date = sdf.parse(name);
                    if(start.getTime()<=date.getTime() && date.getTime()<=end.getTime()){
                        return true;
                    }
                } catch (ParseException e) {
                    Log.e(TAG,"getSys",e);
                }
                return false;
            }
        });


        int offset = 0;

        LogResult result = new LogResult();

        long count = logFiles.length;
        result.setPages(count);
        List<String> urls = new ArrayList<String>();
        List<String> md5s = new ArrayList<String>();
        String reqDir = DeviceUtils.getTmpPath(getContext())+File.separator+RequestContext.getId();
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
    @Override
    public DnloadCmd parser(String payload) {
        return (DnloadCmd) JSonUtils.toObject(payload, DnloadCmd.class);
    }

    @Override
    protected DnloadRes responseWrapper(DnloadCmd cmd, LogResult result) {
        DnloadRes res = new DnloadRes();
        res.setCmdStr(cmd.getCmdStr());
        res.setType(cmd.getType());
        res.setDeviceId(ServiceFactory.getInstance().getDeviceService().getExtSN());
        res.setCode(200);
        res.setMsg("success");

        res.setPages(result.getPages());
        res.setOffset(result.getOffset());
        res.setUrl(result.getUrl());
        res.setMd5(result.getMd5());

        return res;
    }

    @Override
    protected DnloadRes fail(DnloadCmd cmd, String error) {
        DnloadRes res = new DnloadRes();
        res.setType(cmd.getType());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(500);
        res.setMsg(error);
        return res;
    }

    @Override
    protected DnloadRes fail(DnloadCmd cmd, int code, String error) {
        DnloadRes res = new DnloadRes();
        res.setType(cmd.getType());
        res.setCmdStr(cmd.getCmdStr());
        res.setCode(code);
        res.setMsg(error);
        return res;
    }

    @Override
    protected ICmdListener getListener() {
        return null;
    }
}
