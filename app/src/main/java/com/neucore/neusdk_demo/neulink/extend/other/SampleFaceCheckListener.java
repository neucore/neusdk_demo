package com.neucore.neusdk_demo.neulink.extend.other;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.impl.cmd.check.CheckCmd;
import com.neucore.neulink.impl.NeulinkEvent;
import com.neucore.neulink.impl.QueryActionResult;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.MD5Utils;
import com.neucore.neusdk_demo.service.impl.LibManagerService;
import com.neucore.neusdk_demo.service.db.bean.User;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * 人脸检查
 */
public class SampleFaceCheckListener implements ICmdListener<QueryActionResult,CheckCmd> {
    private LibManagerService libManagerService;
    private String TAG = "SampleFaceCheckListener";
    public SampleFaceCheckListener(){
        this.libManagerService = new LibManagerService(ContextHolder.getInstance().getContext());
    }
    @Override
    public QueryActionResult doAction(NeulinkEvent<CheckCmd> event) {

        CheckCmd faceCmd = event.getSource();
        String md5Cloud = faceCmd.getMd5();

        /**
         * 数据库操作
         * @TODO 根据自己需要自行定义，可替换自己的代码
         */
        List<User> users = libManagerService.queryAllUser();
        /**
         * 进行排序
         */
        TreeSet<User> sorted = new TreeSet<User>();
        int len = users==null?0:users.size();
        for(int i=0;i<len;i++){
            User user = users.get(i);
            sorted.add(user);
        }
        StringBuffer cardIds = new StringBuffer();

        int i=0;
        Iterator<User> iterator = sorted.iterator();
        /**
         * 拼接卡号字符串用','连接
         */
        while(iterator.hasNext()){
            User tmp = iterator.next();
            String cardId= tmp.getCardId();
            cardIds.append(cardId);
            if(i<len-1){
                cardIds.append(",");
            }
            i++;
        }
        QueryActionResult result = new QueryActionResult();

        String cardIdStr;

        String md5 = MD5Utils.getInstance().getMD5String(cardIds.toString());
        if(md5Cloud.compareTo(md5)==0){
            cardIdStr = null;
        }
        else{
            cardIdStr = cardIds.toString();
        }

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
