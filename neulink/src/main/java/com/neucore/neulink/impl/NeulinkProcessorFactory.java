package com.neucore.neulink.impl;

import android.content.Context;
import android.util.Log;

import com.neucore.neulink.IProcessor;
import com.neucore.neulink.extend.ICmdListener;
import com.neucore.neulink.extend.ListenerFactory;
import com.neucore.neulink.impl.proc.ALogProcessor;
import com.neucore.neulink.impl.proc.AwakenProcessor;
import com.neucore.neulink.impl.proc.BLibProcessor;
import com.neucore.neulink.impl.proc.BackupProcessor;
import com.neucore.neulink.impl.proc.CfgProcessor;
import com.neucore.neulink.impl.proc.CheckProcessor;
import com.neucore.neulink.impl.proc.DebugProcessor;
import com.neucore.neulink.impl.proc.FirewareProcessor;
import com.neucore.neulink.impl.proc.FirewareProcessorResume;
import com.neucore.neulink.impl.proc.HibrateProcessor;
import com.neucore.neulink.impl.proc.QCfgProcessor;
import com.neucore.neulink.impl.proc.QLibProcessor;
import com.neucore.neulink.impl.proc.QLogProcessor;
import com.neucore.neulink.impl.proc.RebootProcessor;
import com.neucore.neulink.impl.proc.RecoverProcessor;
import com.neucore.neulink.impl.proc.ShellProcessor;

import java.util.concurrent.ConcurrentHashMap;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

public class NeulinkProcessorFactory {

    private static ConcurrentHashMap<String, IProcessor> processors = new ConcurrentHashMap<String,IProcessor>();
    private static String TAG = "NeulinkProcessorFactory";
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
     */
    public synchronized static IProcessor build(Context context,NeulinkTopicParser.Topic topic){
        String biz = topic.getBiz().toLowerCase();
        if(processors.containsKey(biz)){
            return processors.get(biz);
        }
        if("reboot".equalsIgnoreCase(biz)){//设备重启
            processors.put(biz,new RebootProcessor(context));
        }
        else if("firmware".equalsIgnoreCase(biz)){//设备固件升级
            processors.put(biz,new FirewareProcessor(context));
        }
        else if("firmwareresume".equalsIgnoreCase(biz)){//设备固件升级
            processors.put(biz,new FirewareProcessorResume(context));
        }
        else if("hibrate".equalsIgnoreCase(biz)){//设备休眠
            processors.put(biz,new HibrateProcessor(context));
        }
        else if("awaken".equalsIgnoreCase(biz)){//设备唤醒
            processors.put(biz,new AwakenProcessor(context));
        }
        else if("debug".equalsIgnoreCase(biz)){//Shell命令处理器
            processors.put(biz,new DebugProcessor(context));
        }
        else if("shell".equalsIgnoreCase(biz)){//Shell命令处理器
            processors.put(biz,new ShellProcessor(context));
        }
        else if("alog".equalsIgnoreCase(biz)){//算法升级处理器
            processors.put(biz, new ALogProcessor(context));
        }
        else if("qlog".equalsIgnoreCase(biz)){//日志请求处理器
            processors.put(biz, new QLogProcessor(context));
        }
        else if("blib".equalsIgnoreCase(biz)){//目标库批量处理器
            processors.put(biz,new BLibProcessor(context));
        }
        else if("qlib".equalsIgnoreCase(biz)){//目标库单记录处理器
            processors.put(biz,new QLibProcessor(context));
        }
        else if("cfg".equalsIgnoreCase(biz)){//配置管理处理器
            processors.put(biz,new CfgProcessor(context));
        }
        else if("qcfg".equalsIgnoreCase(biz)){//配置管理处理器
            processors.put(biz,new QCfgProcessor(context));
        }
        else if("backup".equalsIgnoreCase(biz)){//备份处理器
            processors.put(biz,new BackupProcessor(context));
        }
        else if("recover".equalsIgnoreCase(biz)){//备份恢复处理器
            processors.put(biz,new RecoverProcessor(context));
        }
        else if("check".equalsIgnoreCase(biz)){//数据校验处理器
            processors.put(biz,new CheckProcessor(context));
        }
        IProcessor processor = processors.get(biz);
        if(ObjectUtil.isNotEmpty(processor)){
            String upperFirst = StrUtil.upperFirst(biz);
            try {
                Class cls = Class.forName("com.neucore.neulink.extend.impl." + upperFirst + "Processor");
                processor = (IProcessor) cls.newInstance();
                processors.put(biz,processor);
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
     * 其他处理器注册
     * @param biz
     * @param processor
     * @param cmdListener
     */
    public static void regist(String biz, IProcessor processor, ICmdListener cmdListener){
        processors.put(biz.toLowerCase(),processor);
        ListenerFactory.getInstance().setExtendListener(biz,cmdListener);
    }
}
