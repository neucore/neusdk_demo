package com.neucore.neulink.impl;

import android.content.Context;

import com.neucore.neulink.IProcessor;
import com.neucore.neulink.impl.proc.ALogProcessor;
import com.neucore.neulink.impl.proc.AwakenProcessor;
import com.neucore.neulink.impl.proc.BLibProcessor;
import com.neucore.neulink.impl.proc.BackupProcessor;
import com.neucore.neulink.impl.proc.CfgProcessor;
import com.neucore.neulink.impl.proc.CheckProcessor;
import com.neucore.neulink.impl.proc.DebugProcessor;
import com.neucore.neulink.impl.proc.FirewareProcessor;
import com.neucore.neulink.impl.proc.HibrateProcessor;
import com.neucore.neulink.impl.proc.QCfgProcessor;
import com.neucore.neulink.impl.proc.QLibProcessor;
import com.neucore.neulink.impl.proc.QLogProcessor;
import com.neucore.neulink.impl.proc.RebootProcessor;
import com.neucore.neulink.impl.proc.RecoverProcessor;
import com.neucore.neulink.impl.proc.ShellProcessor;
import com.neucore.neulink.proc.ReserveProcessor;

import java.util.concurrent.ConcurrentHashMap;

public class NeulinkProcessorFactory {

    private static ConcurrentHashMap<String, IProcessor> processors = new ConcurrentHashMap<String,IProcessor>();

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
        if(processors.containsKey(topic.getBiz())){
            return processors.get(topic.getBiz());
        }
        if("reboot".equalsIgnoreCase(topic.getBiz())){//设备重启
            processors.put(topic.getBiz(),new RebootProcessor(context));
        }
        else if("firmware".equalsIgnoreCase(topic.getBiz())){//设备固件升级
            processors.put(topic.getBiz(),new FirewareProcessor(context));
        }
        else if("hibrate".equalsIgnoreCase(topic.getBiz())){//设备休眠
            processors.put(topic.getBiz(),new HibrateProcessor(context));
        }
        else if("awaken".equalsIgnoreCase(topic.getBiz())){//设备唤醒
            processors.put(topic.getBiz(),new AwakenProcessor(context));
        }
        else if("debug".equalsIgnoreCase(topic.getBiz())){//Shell命令处理器
            processors.put(topic.getBiz(),new DebugProcessor(context));
        }
        else if("shell".equalsIgnoreCase(topic.getBiz())){//Shell命令处理器
            processors.put(topic.getBiz(),new ShellProcessor(context));
        }
        else if("alog".equalsIgnoreCase(topic.getBiz())){//算法升级处理器
            processors.put(topic.getBiz(), new ALogProcessor(context));
        }
        else if("qlog".equalsIgnoreCase(topic.getBiz())){//日志请求处理器
            processors.put(topic.getBiz(), new QLogProcessor(context));
        }
        else if("blib".equalsIgnoreCase(topic.getBiz())){//目标库批量处理器
            processors.put(topic.getBiz(),new BLibProcessor(context));
        }
        else if("qlib".equalsIgnoreCase(topic.getBiz())){//目标库单记录处理器
            processors.put(topic.getBiz(),new QLibProcessor(context));
        }
        else if("cfg".equalsIgnoreCase(topic.getBiz())){//配置管理处理器
            processors.put(topic.getBiz(),new CfgProcessor(context));
        }
        else if("qcfg".equalsIgnoreCase(topic.getBiz())){//配置管理处理器
            processors.put(topic.getBiz(),new QCfgProcessor(context));
        }
        else if("backup".equalsIgnoreCase(topic.getBiz())){//备份处理器
            processors.put(topic.getBiz(),new BackupProcessor(context));
        }
        else if("recover".equalsIgnoreCase(topic.getBiz())){//备份恢复处理器
            processors.put(topic.getBiz(),new RecoverProcessor(context));
        }
        else if("check".equalsIgnoreCase(topic.getBiz())){//数据校验处理器
            processors.put(topic.getBiz(),new CheckProcessor(context));
        }
        else if("reserve".equalsIgnoreCase(topic.getBiz())){//数据校验处理器
            processors.put(topic.getBiz(),new ReserveProcessor(context));
        }
        return processors.get(topic.getBiz());

    }
}
