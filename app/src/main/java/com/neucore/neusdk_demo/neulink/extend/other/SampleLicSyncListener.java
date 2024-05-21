package com.neucore.neusdk_demo.neulink.extend.other;

import com.neucore.NeuSDK.NeuFace;
import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.impl.NeulinkEvent;
import com.neucore.neulink.impl.UpdateActionResult;
import com.neucore.neulink.impl.cmd.rrpc.CarCmd;
import com.neucore.neulink.impl.cmd.rrpc.CarData;
import com.neucore.neulink.impl.cmd.rrpc.KVPair.KeyEnum;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neusdk_demo.neucore.NeuFaceFactory;
import com.neucore.neusdk_demo.service.impl.LibManagerService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 车牌同步
 */
public class SampleLicSyncListener implements ICmdListener<UpdateActionResult, CarCmd> {
    final private String ADD = "add",DEL = "del",UPDATE = "update",SYNC = "sync";
    private LibManagerService libManagerService;
    private NeuFace mNeucore_face;
    private String TAG = "SampleFaceSyncListener";
    public SampleLicSyncListener(){
        this.libManagerService = new LibManagerService(ContextHolder.getInstance().getContext());
        mNeucore_face  = NeuFaceFactory.getInstance().create();
    }
    @Override
    public UpdateActionResult doAction(NeulinkEvent<CarCmd> event) {
        CarCmd cmd = event.getSource();
        String cmdStr = cmd.getCmd();//add：添加|del：删除|update：更新|sync：同步

        long reqTime = cmd.getReqtime();
        /**
         * 总包数
         */
        long pages = cmd.getPages();
        /**
         * 当前第几个包
         */
        long offset = cmd.getOffset();

        /**
         * 获取人脸描述数据
         */
        List<CarData> params = cmd.getDataList();
        /**
         * FaceData 结构介绍
         * ext_id:
         * 访客规则：用逗号连接xxx,中控卡号；
         * eg:vip1,888888 表示VIP访客；
         * eg:n1,666666 表示普通访客【面试人员等】;
         * 正式员工规则：中控卡号
         *
         */
        /**
         * 扩展信息：
         * extInfo：KVPair[]
         */
        /**
         * 名单类型
         */
        KeyEnum type = KeyEnum.Type;
        /**
         * 名单起效时间：unix_timestamp
         */
        KeyEnum start = KeyEnum.PeriodStart;
        /**
         * 名单失效时间：unix_timestamp
         */
        KeyEnum end = KeyEnum.PeriodEnd;
        /**
         * key:ext_id : 卡号;
         * value: Map<String,Object> keys:ext_id,image_type,file
         */
        Map<String, Map<String,Object>> images = cmd.getStringKVMap();

        /**
         * TODO 算法计算特征值
         */

        List failed = (List)images.remove("failed");

        if(ADD.equalsIgnoreCase(cmdStr)||
                UPDATE.equalsIgnoreCase(cmdStr)||
                SYNC.equalsIgnoreCase(cmdStr)){
            /**
             * 数据库操作
             * @TODO 根据自己需要自行定义，可替换自己的代码
             */

        }
        else if(DEL.equalsIgnoreCase(cmdStr)){
            //删除人脸到 twocamera/photo/ 文件夹下

            /**
             * 数据库操作
             * @TODO 根据自己需要自行定义，可替换自己的代码
             */

        }

        /**
         * 表示当前包是最后一个数据包
         */
        if(offset==pages   //最后一个包已经处理完成
                && SYNC.equalsIgnoreCase(cmdStr)){ //同步以云端数据为准，设备端多余的不一致的数据执行删除操作
            /**
             * 最后一个包时，需要执行清理历史数据【无效数据】，可替换自己的代码
             * @TODO 根据自己需要自行定义，可替换自己的代码，建议根据请求时间进行清理；sample根据数据的更新时间进行处理
             */
            
        }

        UpdateActionResult<Map<String,Object>> result = new UpdateActionResult();
        result.setCode(200);
        result.setMessage("success");
        //返回失败的人脸ext_id 列表
        Map<String,Object> datas = new HashMap<String,Object>();
        datas.put("failed",failed);
        result.setData(datas);

        return result;
    }
}
