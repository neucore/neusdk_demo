package com.neucore.neulink.impl;

import android.content.Context;
import android.util.Log;

import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.IProcessor;
import com.neucore.neulink.IResCallback;
import com.neucore.neulink.app.NeulinkConst;
import com.neucore.neulink.impl.proc.DefaultALogProcessor;
import com.neucore.neulink.impl.proc.DefaultAwakenProcessor;
import com.neucore.neulink.impl.proc.DefaultBLibProcessor;
import com.neucore.neulink.impl.proc.DefaultBackupProcessor;
import com.neucore.neulink.impl.proc.DefaultCfgProcessor;
import com.neucore.neulink.impl.proc.DefaultCheckProcessor;
import com.neucore.neulink.impl.proc.DefaultDebugProcessor;
import com.neucore.neulink.impl.proc.DefaultFirewareProcessor;
import com.neucore.neulink.impl.proc.DefaultFirewareProcessorResume;
import com.neucore.neulink.impl.proc.DefaultHibrateProcessor;
import com.neucore.neulink.impl.proc.DefaultQCfgProcessor;
import com.neucore.neulink.impl.proc.DefaultQLibProcessor;
import com.neucore.neulink.impl.proc.DefaultQLogProcessor;
import com.neucore.neulink.impl.proc.DefaultRebootProcessor;
import com.neucore.neulink.impl.proc.DefaultRecoverProcessor;
import com.neucore.neulink.impl.proc.DefaultShellProcessor;

import java.util.concurrent.ConcurrentHashMap;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

public class ProcessRegistrator implements NeulinkConst {
    private static ConcurrentHashMap<String, IProcessor> processors = new ConcurrentHashMap<String,IProcessor>();
    private static String TAG = TAG_PREFIX+"ProcessRegistrator";
    /**
     *
     * 设备重启 rmsg/req/${dev_id}/sys_ctrl/v1.0/${req_no}[/${md5}], qos=0
     * 设备休眠 rmsg/req/${dev_id}/sys_ctrl/v1.0/${req_no}[/${md5}], qos=0
     * 设备唤醒 rmsg/req/${dev_id}/sys_ctrl/v1.0/${req_no}[/${md5}], qos=0
     * 设备方向调整 rmsg/req/${dev_id}/sys_ctrl/v1.0/${req_no}[/${md5}], qos=0
     * 固件升级 rmsg/req/${dev_id}/fireware/v1.0/${req_no}[/${md5}], qos=0
     * Debug设置rmsg/req/${dev_id}/debug/v1.0/${req_no}[/${md5}], qos=0
     *
     * ------------------------------------------------------------------
     *
     * 算法升级            rrpc/req/${dev_id}/alog/v1.0/${req_no}[/${md5}], qos=0
     * 执行shell命令      rrpc/req/${dev_id}/shell/v1.0/${req_no}[/${md5}], qos=0
     * 日志导出           rrpc/req/${dev_id}/rlog/v1.0/${req_no}[/${md5}],qos=0
     * 目标库批量同步      rrpc/req/${dev_id}/blib/v1.0/${req_no}[/${md5}],qos=0
     * 目标库单条写操作    rrpc/req/${dev_id}/lib/v1.0/${req_no}[/${md5}],qos=0
     * 目标库批量查询操作  rrpc/req/ ${dev_id}/lib/${req_no}[/${md5},qos=0
     *
     * 终端配置管理       rrpc/req/${dev_id}/cfg/v1.1/${req_no}[/${md5}],qos=0
     * 查看终端配置       rrpc/req/${dev_id}/cfg/v1.1/${req_no}[/${md5}],qos=0
     *
     * ------------------------------------------------------------------
     *
     * 车牌信息上报响应 upld/res/${dev_id}/carplateinfo/v1.0/${req_no}[/${md5}], qos=0
     * 温度信息上报响应 upld/res/${dev_id}/facetemprature/v1.0/${req_no}[/${md5}], qos=0
     * @deprecated
     */
    public synchronized static IProcessor build(Context context, NeulinkTopicParser.Topic topic){
        String biz = topic.getBiz().toLowerCase();
        if(processors.containsKey(biz)){
            return processors.get(biz);
        }
        if("reboot".equalsIgnoreCase(biz)){//设备重启
            regist(biz,new DefaultRebootProcessor(context));
        }
        else if("firmware".equalsIgnoreCase(biz)){//设备固件升级
            regist(biz,new DefaultFirewareProcessor(context));
        }
        else if("firmwareresume".equalsIgnoreCase(biz)){//设备固件升级
            regist(biz,new DefaultFirewareProcessorResume(context));
        }
        else if("hibrate".equalsIgnoreCase(biz)){//设备休眠
            regist(biz,new DefaultHibrateProcessor(context));
        }
        else if("awaken".equalsIgnoreCase(biz)){//设备唤醒
            regist(biz,new DefaultAwakenProcessor(context));
        }
        else if("debug".equalsIgnoreCase(biz)){//Shell命令处理器
            regist(biz,new DefaultDebugProcessor(context));
        }
        else if("shell".equalsIgnoreCase(biz)){//Shell命令处理器
            regist(biz,new DefaultShellProcessor(context));
        }
        else if("alog".equalsIgnoreCase(biz)){//算法升级处理器
            regist(biz, new DefaultALogProcessor(context));
        }
        else if("qlog".equalsIgnoreCase(biz)){//日志请求处理器
            regist(biz, new DefaultQLogProcessor(context));
        }
        else if("blib".equalsIgnoreCase(biz)){//目标库批量处理器
            regist(biz,new DefaultBLibProcessor(context));
        }
        else if("qlib".equalsIgnoreCase(biz)){//目标库单记录处理器
            regist(biz,new DefaultQLibProcessor(context));
        }
        else if("cfg".equalsIgnoreCase(biz)){//配置管理处理器
            regist(biz,new DefaultCfgProcessor(context));
        }
        else if("qcfg".equalsIgnoreCase(biz)){//配置管理处理器
            regist(biz,new DefaultQCfgProcessor(context));
        }
        else if("backup".equalsIgnoreCase(biz)){//备份处理器
            regist(biz,new DefaultBackupProcessor(context));
        }
        else if("recover".equalsIgnoreCase(biz)){//备份恢复处理器
            regist(biz,new DefaultRecoverProcessor(context));
        }
        else if("check".equalsIgnoreCase(biz)){//数据校验处理器
            regist(biz,new DefaultCheckProcessor(context));
        }
        IProcessor processor = processors.get(biz);
        if(ObjectUtil.isEmpty(processor)){
            String upperFirst = StrUtil.upperFirst(biz);
            try {
                Class cls = Class.forName("com.neucore.neulink.extend.impl." + upperFirst + "Processor");
                processor = (IProcessor) cls.newInstance();
                regist(biz,processor);
            }
            catch (Exception ex){
                Log.e(TAG,ex.getMessage());
            }
        }
        return processor;
    }

    /**
     * 注册扩展处理器
     * @param biz
     * @param processor
     * @deprecated
     */
    public static void regist(String biz,IProcessor processor){
        processors.put(biz.toLowerCase(),processor);
    }

    /**
     * 注册扩展处理器
     * @param biz
     * @param processor
     * @param cmdListener
     */
    public static void regist(String biz, IProcessor processor, ICmdListener cmdListener){
        processors.put(biz.toLowerCase(),processor);
        ListenerRegistrator.getInstance().setExtendListener(biz,cmdListener);
    }

    /**
     * 注册扩展处理器
     * @param biz 业务
     * @param processor 命令解析处理器
     * @param cmdListener 命令到达侦听器
     * @param iResCallback 响应回调
     */
    public static void regist(String biz, IProcessor processor, ICmdListener cmdListener, IResCallback iResCallback){
        processors.put(biz.toLowerCase(),processor);
        ListenerRegistrator.getInstance().setExtendListener(biz,cmdListener);
        CallbackRegistrator.getInstance().setResCallback(biz,iResCallback);
    }
}
