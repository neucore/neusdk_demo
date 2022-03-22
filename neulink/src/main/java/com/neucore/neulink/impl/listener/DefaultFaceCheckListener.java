package com.neucore.neulink.impl.listener;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.cmd.cfg.CfgCmd;
import com.neucore.neulink.cmd.check.CheckCmd;
import com.neucore.neulink.extend.ActionResult;
import com.neucore.neulink.extend.NeulinkEvent;
import com.neucore.neulink.extend.QueryActionResult;
import com.neucore.neulink.util.MD5Utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class DefaultFaceCheckListener implements ICmdListener<QueryActionResult, CheckCmd> {
    @Override
    public QueryActionResult doAction(NeulinkEvent<CheckCmd> event) {

        CheckCmd faceCmd = event.getSource();
        String md5Cloud = faceCmd.getMd5();


        QueryActionResult result = new QueryActionResult();

        String cardIdStr = new String();

        /**
         * @TODO: 待实现
         */

        result.setCode(200);
        result.setMessage("success");
        //返回失败的人脸ext_id 列表
        Map<String,Object> datas = new HashMap<String,Object>();
        /**
         * card_ids key值不能变
         */
        datas.put("card_ids",cardIdStr);
        result.setData(datas);
        return result;
    }
}
