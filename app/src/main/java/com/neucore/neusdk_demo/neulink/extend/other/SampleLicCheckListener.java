package com.neucore.neusdk_demo.neulink.extend.other;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.impl.NeulinkEvent;
import com.neucore.neulink.impl.QueryActionResult;
import com.neucore.neulink.impl.cmd.check.CheckCmd;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.MD5Utils;
import com.neucore.neusdk_demo.service.impl.LibManagerService;

import java.util.HashMap;
import java.util.Map;

/**
 * 车牌检查
 */
public class SampleLicCheckListener implements ICmdListener<QueryActionResult,CheckCmd> {

    private String TAG = "SampleFaceCheckListener";
    public SampleLicCheckListener(){

    }
    @Override
    public QueryActionResult doAction(NeulinkEvent<CheckCmd> event) {

        CheckCmd faceCmd = event.getSource();
        String md5Cloud = faceCmd.getMd5();


        QueryActionResult result = new QueryActionResult();
        /**
         * TODO 比对，如果不一致，则返回所有车辆号码
         */
        String ids = "....";

        String md5 = MD5Utils.getInstance().getMD5String(ids.toString());
        if(md5Cloud.compareTo(md5)==0){
            ids = null;
        }
        else{
            ids = ids.toString();
        }

        result.setCode(200);
        result.setMessage("success");
        //返回失败的人脸ext_id 列表
        Map<String,Object> datas = new HashMap<String,Object>();
        /**
         * card_ids key值不能变
         */
        datas.put("ids",ids);
        result.setData(datas);
        return result;
    }
}
