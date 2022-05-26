package com.neucore.neulink.impl.registry;

import android.content.Context;
import android.util.Log;

import com.neucore.neulink.IBlib$ObjtypeProcessor;
import com.neucore.neulink.IClib$ObjtypeProcessor;
import com.neucore.neulink.ICmdListener;
import com.neucore.neulink.IProcessor;
import com.neucore.neulink.IQlib$ObjtypeProcessor;
import com.neucore.neulink.IResCallback;
import com.neucore.neulink.NeulinkConst;
import com.neucore.neulink.impl.DefaultBLibSyncProcessor;
import com.neucore.neulink.impl.DefaultCLibProcessor;
import com.neucore.neulink.impl.DefaultQLibProcessor;
import com.neucore.neulink.impl.NeulinkTopicParser;
import com.neucore.neulink.impl.proc.DefaultALogProcessor;
import com.neucore.neulink.impl.proc.DefaultAwakenProcessor;
import com.neucore.neulink.impl.proc.DefaultBackupProcessor;
import com.neucore.neulink.impl.proc.DefaultCfgProcessor;
import com.neucore.neulink.impl.proc.DefaultDebugProcessor;
import com.neucore.neulink.impl.proc.DefaultFirewareProcessor;
import com.neucore.neulink.impl.proc.DefaultFirewareProcessorResume;
import com.neucore.neulink.impl.proc.DefaultHibrateProcessor;
import com.neucore.neulink.impl.proc.DefaultQCfgProcessor;
import com.neucore.neulink.impl.proc.DefaultQLogProcessor;
import com.neucore.neulink.impl.proc.DefaultRebootProcessor;
import com.neucore.neulink.impl.proc.DefaultRecoverProcessor;
import com.neucore.neulink.impl.proc.DefaultShellProcessor;

import java.util.concurrent.ConcurrentHashMap;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

public class ProcessRegistry implements NeulinkConst {
    private static ConcurrentHashMap<String, IProcessor> processors = new ConcurrentHashMap<String,IProcessor>();
    private static ConcurrentHashMap<String, IBlib$ObjtypeProcessor> blibBatchProcessors = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, IQlib$ObjtypeProcessor> qlibBatchProcessors = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, IClib$ObjtypeProcessor> clibBatchProcessors = new ConcurrentHashMap<>();
    private static String TAG = TAG_PREFIX+"ProcessRegistry";
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
        if(NEULINK_BIZ_REBOOT.equalsIgnoreCase(biz)){//设备重启
            regist(biz,new DefaultRebootProcessor(context));
        }
        else if(NEULINK_BIZ_FIRMWARE.equalsIgnoreCase(biz)){//设备固件升级
            regist(biz,new DefaultFirewareProcessor(context));
        }
        else if(NEULINK_BIZ_FIRMWARE_RESUME.equalsIgnoreCase(biz)){//设备固件升级
            regist(biz,new DefaultFirewareProcessorResume(context));
        }
        else if(NEULINK_BIZ_HIBRATE.equalsIgnoreCase(biz)){//设备休眠
            regist(biz,new DefaultHibrateProcessor(context));
        }
        else if(NEULINK_BIZ_AWAKEN.equalsIgnoreCase(biz)){//设备唤醒
            regist(biz,new DefaultAwakenProcessor(context));
        }
        else if(NEULINK_BIZ_DEBUG.equalsIgnoreCase(biz)){//Shell命令处理器
            regist(biz,new DefaultDebugProcessor(context));
        }
        else if(NEULINK_BIZ_SHELL.equalsIgnoreCase(biz)){//Shell命令处理器
            regist(biz,new DefaultShellProcessor(context));
        }
        else if(NEULINK_BIZ_ALOG.equalsIgnoreCase(biz)){//算法升级处理器
            regist(biz, new DefaultALogProcessor(context));
        }
        else if(NEULINK_BIZ_QLOG.equalsIgnoreCase(biz)){//日志请求处理器
            regist(biz, new DefaultQLogProcessor(context));
        }
        else if(NEULINK_BIZ_BLIB.equalsIgnoreCase(biz)){//目标库批量处理器
            regist(biz,new DefaultBLibSyncProcessor(context));
        }
        else if(NEULINK_BIZ_QLIB.equalsIgnoreCase(biz)){//目标库单记录处理器
            regist(biz,new DefaultQLibProcessor(context));
        }
        else if(NEULINK_BIZ_CFG.equalsIgnoreCase(biz)){//配置管理处理器
            regist(biz,new DefaultCfgProcessor(context));
        }
        else if(NEULINK_BIZ_QCFG.equalsIgnoreCase(biz)){//配置管理处理器
            regist(biz,new DefaultQCfgProcessor(context));
        }
        else if(NEULINK_BIZ_BACKUP.equalsIgnoreCase(biz)){//备份处理器
            regist(biz,new DefaultBackupProcessor(context));
        }
        else if(NEULINK_BIZ_RECOVER.equalsIgnoreCase(biz)){//备份恢复处理器
            regist(biz,new DefaultRecoverProcessor(context));
        }
        else if(NEULINK_BIZ_CLIB.equalsIgnoreCase(biz)){//数据校验处理器
            regist(biz,new DefaultCLibProcessor(context));
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
     *
     * @param objType
     * @param batchProcessor
     * @param cmdListener
     */
    public static void registBlibBatch(String objType,IBlib$ObjtypeProcessor batchProcessor,ICmdListener cmdListener){
        String batchBiz = NEULINK_BIZ_BLIB+"."+objType.toLowerCase();
        blibBatchProcessors.put(batchBiz,batchProcessor);
        ListenerRegistry.getInstance().setExtendListener(batchBiz,cmdListener);
    }

    /**
     *
     * @param objType
     * @return
     */
    public static IBlib$ObjtypeProcessor getBlibBatch(String objType){
        String batchBiz = NEULINK_BIZ_BLIB+"."+objType.toLowerCase();
        return blibBatchProcessors.get(batchBiz);
    }

    /**
     *
     * @param objType
     * @param batchProcessor
     * @param cmdListener
     */
    public static void registQlibBatch(String objType, IQlib$ObjtypeProcessor batchProcessor, ICmdListener cmdListener){
        String batchBiz = NEULINK_BIZ_QLIB+"."+objType.toLowerCase();
        qlibBatchProcessors.put(batchBiz,batchProcessor);
        ListenerRegistry.getInstance().setExtendListener(batchBiz,cmdListener);
    }

    /**
     *
     * @param objType
     * @return
     */
    public static IQlib$ObjtypeProcessor getQlibBatch(String objType){
        String batchBiz = NEULINK_BIZ_QLIB+"."+objType.toLowerCase();
        return qlibBatchProcessors.get(batchBiz);
    }

    /**
     *
     * @param objType
     * @param batchProcessor
     * @param cmdListener
     */
    public static void registClibBatch(String objType,IClib$ObjtypeProcessor batchProcessor,ICmdListener cmdListener){
        String batchBiz = NEULINK_BIZ_CLIB+"."+objType.toLowerCase();
        clibBatchProcessors.put(batchBiz,batchProcessor);
        ListenerRegistry.getInstance().setExtendListener(batchBiz,cmdListener);
    }

    /**
     *
     * @param objType
     * @return
     */
    public static IClib$ObjtypeProcessor getClibBatch(String objType){
        String batchBiz = NEULINK_BIZ_CLIB+"."+objType.toLowerCase();
        return clibBatchProcessors.get(batchBiz);
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
        biz = biz.toLowerCase();
        processors.put(biz,processor);
        ListenerRegistry.getInstance().setExtendListener(biz,cmdListener);
    }

    /**
     * 注册扩展处理器
     * @param biz 业务
     * @param processor 命令解析处理器
     * @param cmdListener 命令到达侦听器
     * @param iResCallback 响应回调
     */
    public static void regist(String biz, IProcessor processor, ICmdListener cmdListener, IResCallback iResCallback){
        biz = biz.toLowerCase();
        processors.put(biz,processor);
        ListenerRegistry.getInstance().setExtendListener(biz,cmdListener);
        CallbackRegistry.getInstance().setResCallback(biz,iResCallback);
    }
}
