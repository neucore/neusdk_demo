package com.neucore.neusdk_demo.neulink.extend.other;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.impl.NeulinkEvent;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.impl.QueryActionResult;
import com.neucore.neulink.impl.StorageFactory;
import com.neucore.neulink.impl.cmd.rrpc.QResult;
import com.neucore.neulink.impl.cmd.rrpc.TLibQueryCmd;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.MD5Utils;
import com.neucore.neulink.util.RequestContext;
import com.neucore.neusdk_demo.service.db.UserDaoUtils;
import com.neucore.neusdk_demo.service.db.bean.User;
import com.neucore.neusdk_demo.service.impl.LibManagerService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 车辆查询
 */
public class SampleCarQueryListener implements ICmdListener<QueryActionResult,TLibQueryCmd> {

    private String TAG = "SampleFaceQueryListener";
    public SampleCarQueryListener(){

    }
    @Override
    public QueryActionResult doAction(NeulinkEvent<TLibQueryCmd> event) {

        TLibQueryCmd cmd = event.getSource();
        /**
         * TODO 查询
         */
        List<String> urls = new ArrayList<String>();
        List<String> md5s = new ArrayList<String>();
        /**
         * TODO 实现查询
         */
        return new QueryActionResult();
    }

    protected File store(TLibQueryCmd cmd,String dataPath, int index, Object[] dataArray) throws IOException {
        String path = ContextHolder.getInstance().getContext().getFilesDir() + "/" + dataPath + "/" + cmd.getReqNo() + "/";
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
}
