package com.neucore.neusdk_demo.neulink.extend.other;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.impl.cmd.rrpc.QResult;
import com.neucore.neulink.impl.cmd.rrpc.TLibQueryCmd;
import com.neucore.neulink.impl.NeulinkEvent;
import com.neucore.neulink.impl.QueryActionResult;
import com.neucore.neulink.impl.StorageFactory;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.JSonUtils;
import com.neucore.neulink.util.MD5Utils;
import com.neucore.neulink.util.RequestContext;
import com.neucore.neusdk_demo.service.impl.LibManagerService;
import com.neucore.neusdk_demo.service.db.UserDaoUtils;
import com.neucore.neusdk_demo.service.db.bean.User;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SampleFaceQueryListener implements ICmdListener<QueryActionResult,TLibQueryCmd> {
    private LibManagerService libManagerService;
    private UserDaoUtils userDaoUtils = null;
    private String TAG = "SampleFaceQueryListener";
    public SampleFaceQueryListener(){
        this.libManagerService = new LibManagerService(ContextHolder.getInstance().getContext());
    }
    @Override
    public QueryActionResult doAction(NeulinkEvent<TLibQueryCmd> event) {

        TLibQueryCmd cmd = event.getSource();

        long count = userDaoUtils.count(cmd.getConds());

        long page = count/200;
        long mod = count%200;
        if(mod>0){
            page = page +1;
        }

        List<String> urls = new ArrayList<String>();
        List<String> md5s = new ArrayList<String>();
        QResult result = new QResult();
        if(page>0){
            for(int i=1;i<page+1;i++){
                /**
                 * 数据库操作
                 * @TODO 根据自己需要自行定义，可替换自己的代码
                 */
                List<User> dataList = userDaoUtils.query(cmd.getConds(),i-1);
                User[] dataArray = new User[dataList.size()];
                dataList.toArray(dataArray);
                File localFile = null;
                try {
                    /**
                     * 上传到存储服务可以根据
                     */
                    localFile = store(cmd,"users", i, dataArray);
                }
                catch (Exception ex){}
                String md5 = null;
                try {
                    md5 = MD5Utils.getInstance().getMD5File(localFile.getAbsolutePath());
                }
                catch (Exception ex){}
                String url = StorageFactory.getInstance().create().uploadQData(localFile.getAbsolutePath(), RequestContext.getId(),i);
                md5s.add(md5);
                urls.add(url);
                localFile.delete();
            }
            result.setCount(count);
            result.setPage(page);
            result.setOffset(1);
            result.setUrl(urls.get(0));
            result.setMd5(md5s.get(0));
        }

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
