package com.neucore.neulink.impl.listener;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.impl.cmd.check.CheckCmd;
import com.neucore.neulink.impl.NeulinkEvent;
import com.neucore.neulink.impl.QueryActionResult;

import java.util.HashMap;
import java.util.Map;

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
