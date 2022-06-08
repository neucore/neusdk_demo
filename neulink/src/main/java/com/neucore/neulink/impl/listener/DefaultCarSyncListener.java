package com.neucore.neulink.impl.listener;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.neucore.neulink.log.LogUtils;
import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.impl.NeulinkEvent;
import com.neucore.neulink.impl.cmd.rrpc.CarCmd;
import com.neucore.neulink.impl.cmd.rrpc.CarData;
import com.neucore.neulink.impl.cmd.rrpc.FaceData;
import com.neucore.neulink.impl.cmd.rrpc.FaceNode;
import com.neucore.neulink.impl.cmd.rrpc.CarPkgActionResult;
import com.neucore.neulink.impl.cmd.rrpc.KVPair;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.ObjectUtil;


public class DefaultCarSyncListener implements ICmdListener<CarPkgActionResult,CarCmd> {

    protected String TAG = TAG_PREFIX+this.getClass().getSimpleName();

    final private String OBJ_TYPE_FACE = "face";

    final private String ADD = "add",DEL = "del",UPDATE = "update",SYNC = "sync",PUSH = "push";
    private String libDir;
    public DefaultCarSyncListener(){
        libDir = DeviceUtils.getTmpPath(ContextHolder.getInstance().getContext())+"/libDir";
    }

    @Override
    public CarPkgActionResult doAction(NeulinkEvent<CarCmd> event) {
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
        List<CarData> params = cmd.getData();
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
        KVPair.KeyEnum type = KVPair.KeyEnum.Type;
        /**
         * 名单起效时间：unix_timestamp
         */
        KVPair.KeyEnum start = KVPair.KeyEnum.PeriodStart;
        /**
         * 名单失效时间：unix_timestamp
         */
        KVPair.KeyEnum end = KVPair.KeyEnum.PeriodEnd;

        Map<String, Map<String,Object>> images = cmd.getStringKVMap();

        /**
         * 算法介入：特征值
         */
        List failed = (List)images.remove("failed");

        if(ADD.equalsIgnoreCase(cmdStr)||
                UPDATE.equalsIgnoreCase(cmdStr)||
                SYNC.equalsIgnoreCase(cmdStr)){
            //保存人脸到 twocamera/photo/ 文件夹下

            /**
             * 数据库操作
             * @TODO 根据自己需要自行定义，可替换自己的代码
             */
            LogUtils.eTag(TAG,cmdStr+"持久化没有实现...");
        }
        else if(DEL.equalsIgnoreCase(cmdStr)){
            //删除人脸到 twocamera/photo/ 文件夹下

            /**
             * 数据库操作
             * @TODO 根据自己需要自行定义，可替换自己的代码
             */
            LogUtils.eTag(TAG,cmdStr+"持久化为实现。。。");
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
            LogUtils.eTag(TAG,"清除过期数据没有实现。。。");
        }

        CarPkgActionResult result = new CarPkgActionResult();
        result.setCode(200);
        result.setMessage("success");
        result.setData(failed);
        return result;
    }
}
