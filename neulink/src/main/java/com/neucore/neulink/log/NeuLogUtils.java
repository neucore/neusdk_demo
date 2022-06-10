package com.neucore.neulink.log;

import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * NeuLogUtils 工具类
 *
 * @author Administrator
 *
 */
@SuppressWarnings("all")
public class NeuLogUtils {

    /** 这里的AppName决定log的文件位置和名称 **/
    private static final String APP_NAME = "neulink";

    /** 设置log文件全路径，这里是 MyApp/Log/myapp.log **/
    private static final String LOG_FILE_PATH = DeviceUtils.getLogPath(ContextHolder.getInstance().getContext())+File.separator+APP_NAME+".log";

    /**
     *    ### log文件的格式
     *
     *    ### 输出格式解释：
     *    ### [%-d{yyyy-MM-dd HH:mm:ss}][Class: %c.%M(%F:%L)] %n[Level: %-5p] - Msg: %m%n
     *
     *    ### %d{yyyy-MM-dd HH:mm:ss}: 时间，大括号内是时间格式
     *    ### %c: 全类名
     *    ### %M: 调用的方法名称
     *    ### %F:%L  类名:行号（在控制台可以追踪代码）
     *    ### %n: 换行
     *    ### %p: 日志级别，这里%-5p是指定的5个字符的日志名称，为的是格式整齐
     *    ### %m: 日志信息

     *    ### 输出的信息大概如下：
     *    ### [时间{时间格式}][信息所在的class.method(className：lineNumber)] 换行
     *    ### [Level: 5个字符的等级名称] - Msg: 输出信息 换行
     */
    private static final String LOG_FILE_PATTERN = "[%-d{yyyy-MM-dd HH:mm:ss}] - Msg: %m%n";

    /** 生产环境下的log等级 **/
    private static final Level LOG_LEVEL_PRODUCE = Level.ALL;

    /** 发布以后的log等级 **/
    private static final Level LOG_LEVEL_RELEASE = Level.INFO;

    static {
        configLog();
    }

    /**
     * 配置log4j参数
     */
    public static void configLog(){

        LogConfig logConfigurator = new LogConfig();

        /** 设置Log等级，生产环境下调用setLogToProduce()，发布后调用setLogToRelease() **/
        setLogToRelease(logConfigurator);
        boolean created = false;
        try {
            created = new File(LOG_FILE_PATH).createNewFile();
        }
        catch (Exception ex){}
        logConfigurator.setFileName(LOG_FILE_PATH);

        //设置root日志输出级别 默认为DEBUG
        logConfigurator.setRootLevel(Level.INFO);
        // 设置日志输出级别
        logConfigurator.setLevel("com.neucore.neulink", Level.DEBUG);
        //设置 输出到日志文件的文字格式 默认 %d %-5p [%c{2}]-[%L] %m%n
        logConfigurator.setFilePattern(LOG_FILE_PATTERN);
        //设置输出到控制台的文字格式 默认%m%n
        logConfigurator.setLogCatPattern(LOG_FILE_PATTERN);
        //设置总文件大小 (1M)
        logConfigurator.setMaxFileSize(1024 * 1024);
        //设置最大产生的文件个数
        logConfigurator.setMaxBackupSize(3);
        //是否立即刷新
        logConfigurator.setImmediateFlush(true);
        //设置所有消息是否被立刻输出 默认为true,false 不输出
        logConfigurator.setImmediateFlush(true);
        //是否本地控制台打印输出 默认为true ，false不输出
        logConfigurator.setUseLogCatAppender(true);
        //设置是否启用文件附加,默认为true。false为覆盖文件
        logConfigurator.setUseFileAppender(true);
        //设置是否重置配置文件，默认为true
        logConfigurator.setResetConfiguration(true);
        //是否显示内部初始化日志,默认为false
        logConfigurator.setInternalDebugging(false);

        logConfigurator.configure();
    }

    /**
     * 将log设置为生产环境
     *
     * @param logConfig
     */
    private static void setLogToProduce(LogConfig logConfig) {
        logConfig.setRootLevel(LOG_LEVEL_PRODUCE);
    }

    /**
     * 将log设置为发布以后的环境
     *
     * @param logConfig
     */
    private static void setLogToRelease(LogConfig logConfig) {
        logConfig.setRootLevel(LOG_LEVEL_RELEASE);
    }

    public static void eTag(String tag,final Object... contents){
        Logger logger = Logger.getLogger(tag);
        logger.error(contents[0],(Throwable) contents[1]);
    }

    public static void wTag(String tag,final Object... contents){
        Logger logger = Logger.getLogger(tag);
        logger.warn(contents[0]);
    }

    public static void iTag(String tag,final Object... contents){
        Logger logger = Logger.getLogger(tag);
        logger.info(contents[0]);
    }

    public static void dTag(String tag,final Object... contents){
        Logger logger = Logger.getLogger(tag);
        logger.debug(contents[0]);
    }

    public static void fTag(String tag,final Object... contents){
        Logger logger = Logger.getLogger(tag);
        logger.fatal(contents[0]);
    }

    public static void tTag(String tag,final Object... contents){
        Logger logger = Logger.getLogger(tag);
        logger.trace(contents[0]);
    }
}