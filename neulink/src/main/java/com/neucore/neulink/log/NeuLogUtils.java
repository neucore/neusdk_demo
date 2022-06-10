package com.neucore.neulink.log;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * NeuLogUtils 工具类
 *
 * @author Administrator
 *
 */
@SuppressWarnings("all")
public class NeuLogUtils {

    static {
        configLog();
    }
    /**
     * 配置log4j参数
     */
    public static void configLog(){

        NeuLogConfig logConfigurator = new NeuLogConfig();

        logConfigurator.setFileName(NeuLogConfig.LOG_FILE_PATH);

        //设置root日志输出级别 默认为DEBUG
        logConfigurator.setRootLevel(NeuLogConfig.LOG_LEVEL_RELEASE);
        // 设置日志输出级别
        logConfigurator.setLevel("com.neucore.neulink", Level.DEBUG);
        //设置 输出到日志文件的文字格式 默认 %d %-5p [%c{2}]-[%L] %m%n
        logConfigurator.setFilePattern(NeuLogConfig.filePattern);
        //设置输出到控制台的文字格式 默认%m%n
        logConfigurator.setLogCatPattern(NeuLogConfig.logCatPattern);
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

        /** 设置Log等级，生产环境下调用setLogToProduce()，发布后调用setLogToRelease() **/
        setLogToRelease(logConfigurator);
    }
    /**
     * 将log设置为生产环境
     *
     * @param neuLogConfig
     */
    private static void setLogToProduce(NeuLogConfig neuLogConfig) {
        neuLogConfig.setRootLevel(NeuLogConfig.LOG_LEVEL_PRODUCE);
    }

    /**
     * 将log设置为发布以后的环境
     *
     * @param neuLogConfig
     */
    private static void setLogToRelease(NeuLogConfig neuLogConfig) {
        neuLogConfig.setRootLevel(NeuLogConfig.LOG_LEVEL_RELEASE);
    }

    public static void eTag(String tag,Object message){
        Logger logger = Logger.getLogger(tag);
        logger.error(message);
    }

    public static void eTag(String tag,Object message,Throwable throwable){
        Logger logger = Logger.getLogger(tag);
        logger.error(message,throwable);
    }

    public static void wTag(String tag,Object message){
        Logger logger = Logger.getLogger(tag);
        logger.warn(message);
    }

    public static void iTag(String tag,Object message){
        Logger logger = Logger.getLogger(tag);
        logger.info(message);
    }

    public static void dTag(String tag,Object message){
        Logger logger = Logger.getLogger(tag);
        logger.debug(message);
    }

    public static void fTag(String tag,Object message){
        Logger logger = Logger.getLogger(tag);
        logger.fatal(message);
    }

    public static void tTag(String tag,Object message){
        Logger logger = Logger.getLogger(tag);
        logger.trace(message);
    }
}