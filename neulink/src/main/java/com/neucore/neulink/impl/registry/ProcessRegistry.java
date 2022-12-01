package com.neucore.neulink.impl.registry;

import android.content.Context;

import com.neucore.neulink.IBLibSyncProcessor;
import com.neucore.neulink.ICLibProcessor;
import com.neucore.neulink.IQLibProcessor;
import com.neucore.neulink.impl.listener.DefaultCarCheckListener;
import com.neucore.neulink.impl.listener.DefaultCarQueryListener;
import com.neucore.neulink.impl.listener.DefaultCarSyncListener;
import com.neucore.neulink.impl.listener.DefaultFaceCheckListener;
import com.neucore.neulink.impl.listener.DefaultFaceQueryListener;
import com.neucore.neulink.impl.listener.DefaultFaceSyncListener;
import com.neucore.neulink.impl.listener.DefaultLicCheckListener;
import com.neucore.neulink.impl.listener.DefaultLicQueryListener;
import com.neucore.neulink.impl.listener.DefaultLicSyncListener;
import com.neucore.neulink.impl.proc.DefaultCarCheckProcessor;
import com.neucore.neulink.impl.proc.DefaultCarQueryProcessor;
import com.neucore.neulink.impl.proc.DefaultCarSyncProcessor;
import com.neucore.neulink.impl.proc.DefaultFaceCheckProcessor;
import com.neucore.neulink.impl.proc.DefaultFaceQueryProcessor;
import com.neucore.neulink.impl.proc.DefaultFaceSyncProcessor;
import com.neucore.neulink.impl.proc.DefaultLicCheckProcessor;
import com.neucore.neulink.impl.proc.DefaultLicQueryProcessor;
import com.neucore.neulink.impl.proc.DefaultLicSyncProcessor;
import com.neucore.neulink.impl.proc.DefaultResetProcessor;
import com.neucore.neulink.log.NeuLogUtils;
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
import com.neucore.neulink.util.ContextHolder;

import java.util.concurrent.ConcurrentHashMap;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

public final class ProcessRegistry implements NeulinkConst {
    private static ConcurrentHashMap<String, IProcessor> processors = new ConcurrentHashMap<String,IProcessor>();
    private static ConcurrentHashMap<String, IBlib$ObjtypeProcessor> blibObjtypeProcessors = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, IQlib$ObjtypeProcessor> qlibObjtypeProcessors = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, IClib$ObjtypeProcessor> clibObjtypeProcessors = new ConcurrentHashMap<>();
    private static String TAG = TAG_PREFIX+"ProcessRegistry";

    static {
        Context context = ContextHolder.getInstance().getContext();

        regist(NEULINK_BIZ_REBOOT,new DefaultRebootProcessor(context));
        regist(NEULINK_BIZ_SHELL,new DefaultShellProcessor(context));
        regist(NEULINK_BIZ_AWAKEN,new DefaultAwakenProcessor(context));
        regist(NEULINK_BIZ_HIBRATE,new DefaultHibrateProcessor(context));
        regist(NEULINK_BIZ_ALOG,new DefaultALogProcessor(context));
        regist(NEULINK_BIZ_FIRMWARE,new DefaultFirewareProcessor(context));
        regist(NEULINK_BIZ_FIRMWARE_RESUME,new DefaultFirewareProcessorResume(context));
        regist(NEULINK_BIZ_BACKUP,new DefaultBackupProcessor(context));
        regist(NEULINK_BIZ_RECOVER,new DefaultRecoverProcessor(context));
        regist(NEULINK_BIZ_RESET,new DefaultResetProcessor(context));
        regist(NEULINK_BIZ_DEBUG,new DefaultDebugProcessor(context));
        regist(NEULINK_BIZ_QLOG,new DefaultQLogProcessor(context));
        regist(NEULINK_BIZ_CFG,new DefaultCfgProcessor(context));
        regist(NEULINK_BIZ_QCFG,new DefaultQCfgProcessor(context));


        /**
         * 批处理
         */
        registBlibProcessor(new DefaultBLibSyncProcessor(context));
        registBlib$ObjtypeProcessor(NEULINK_BIZ_OBJTYPE_FACE,new DefaultFaceSyncProcessor(),new DefaultFaceSyncListener());
        registBlib$ObjtypeProcessor(NEULINK_BIZ_OBJTYPE_CAR,new DefaultCarSyncProcessor(),new DefaultCarSyncListener());
        registBlib$ObjtypeProcessor(NEULINK_BIZ_OBJTYPE_LIC,new DefaultLicSyncProcessor(),new DefaultLicSyncListener());

        registQlibProcessor(new DefaultQLibProcessor(context));
        registQlib$ObjtypeProcessor(NEULINK_BIZ_OBJTYPE_FACE,new DefaultFaceQueryProcessor(),new DefaultFaceQueryListener());
        registQlib$ObjtypeProcessor(NEULINK_BIZ_OBJTYPE_CAR,new DefaultCarQueryProcessor(),new DefaultCarQueryListener());
        registQlib$ObjtypeProcessor(NEULINK_BIZ_OBJTYPE_LIC,new DefaultLicQueryProcessor(),new DefaultLicQueryListener());

        registClibProcessor(new DefaultCLibProcessor(context));
        registClib$ObjtypeProcessor(NEULINK_BIZ_OBJTYPE_FACE,new DefaultFaceCheckProcessor(),new DefaultFaceCheckListener());
        registClib$ObjtypeProcessor(NEULINK_BIZ_OBJTYPE_CAR,new DefaultCarCheckProcessor(),new DefaultCarCheckListener());
        registClib$ObjtypeProcessor(NEULINK_BIZ_OBJTYPE_LIC,new DefaultLicCheckProcessor(),new DefaultLicCheckListener());
    }

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
     *
     */
    public synchronized static IProcessor build(Context context, String biz){
        if(processors.containsKey(biz)){
            return processors.get(biz);
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
                NeuLogUtils.eTag(TAG,ex.getMessage());
            }
        }
        return processor;
    }

    /**
     * 注册扩展处理器
     * @param biz
     * @param processor
     */
    private static void regist(String biz,IProcessor processor){
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

    /**
     *
      * @param bLibSyncProcessor
     */
    public static void registBlibProcessor(IBLibSyncProcessor bLibSyncProcessor){
        regist(NEULINK_BIZ_BLIB,bLibSyncProcessor);
    }
    /**
     *
     * @param objType
     * @param objtypeProcessor
     * @param cmdListener
     */
    public static void registBlib$ObjtypeProcessor(String objType, IBlib$ObjtypeProcessor objtypeProcessor, ICmdListener cmdListener){
        String batchBiz = NEULINK_BIZ_BLIB+"."+objType.toLowerCase();
        blibObjtypeProcessors.put(batchBiz,objtypeProcessor);
        ListenerRegistry.getInstance().setBlibExtendListener(objType,cmdListener);
    }

    /**
     *
     * @param objType
     * @return
     */
    public static IBlib$ObjtypeProcessor getBlib$ObjtypeProcessor(String objType){
        String batchBiz = NEULINK_BIZ_BLIB+"."+objType.toLowerCase();
        return blibObjtypeProcessors.get(batchBiz);
    }

    /**
     *
     * @param qLibSyncProcessor
     */
    public static void registQlibProcessor(IQLibProcessor qLibSyncProcessor){
        processors.put(NEULINK_BIZ_QLIB,qLibSyncProcessor);
    }

    /**
     *
     * @param objType
     * @return
     */
    public static IQlib$ObjtypeProcessor getQlibProcessor(String objType){
        String batchBiz = NEULINK_BIZ_QLIB+"."+objType.toLowerCase();
        return qlibObjtypeProcessors.get(batchBiz);
    }

    /**
     *
     * @param objType
     * @param objtypeProcessor
     * @param cmdListener
     */
    public static void registQlib$ObjtypeProcessor(String objType, IQlib$ObjtypeProcessor objtypeProcessor, ICmdListener cmdListener){
        String batchBiz = NEULINK_BIZ_QLIB+"."+objType.toLowerCase();
        qlibObjtypeProcessors.put(batchBiz,objtypeProcessor);
        ListenerRegistry.getInstance().setQlibExtendListener(objType,cmdListener);
    }

    /**
     *
     * @param objType
     * @return
     */
    public static IQlib$ObjtypeProcessor getQlib$ObjtypeProcessor(String objType){
        String batchBiz = NEULINK_BIZ_BLIB+"."+objType.toLowerCase();
        return qlibObjtypeProcessors.get(batchBiz);
    }
    /**
     *
     * @param cLibProcessor
     */
    public static void registClibProcessor(ICLibProcessor cLibProcessor){
        processors.put(NEULINK_BIZ_CLIB,cLibProcessor);
    }

    /**
     *
     * @param objType
     * @param batchProcessor
     * @param cmdListener
     */
    public static void registClib$ObjtypeProcessor(String objType, IClib$ObjtypeProcessor batchProcessor, ICmdListener cmdListener){
        String batchBiz = NEULINK_BIZ_CLIB+"."+objType.toLowerCase();
        clibObjtypeProcessors.put(batchBiz,batchProcessor);
        ListenerRegistry.getInstance().setClibExtendListener(objType,cmdListener);
    }

    /**
     *
     * @param objType
     * @return
     */
    public static IClib$ObjtypeProcessor getClib$ObjtypeProcessor(String objType){
        String batchBiz = NEULINK_BIZ_BLIB+"."+objType.toLowerCase();
        return clibObjtypeProcessors.get(batchBiz);
    }
    /**
     *
     * @param objType
     * @return
     */
    public static IClib$ObjtypeProcessor getClibProcessor(String objType){
        String batchBiz = NEULINK_BIZ_CLIB+"."+objType.toLowerCase();
        return clibObjtypeProcessors.get(batchBiz);
    }
}
