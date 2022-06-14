package com.neucore.neulink.log;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.concurrent.locks.ReentrantLock;

/**
 * NeuLogUtils 工具类
 *
 * @author Administrator
 *
 */
@SuppressWarnings("all")
public class NeuLogUtils {
    private static NeuLogConfig logConfigurator = new NeuLogConfig();
    private static boolean init = false;
    private static ReentrantLock reentrantLock = new ReentrantLock();

    /**
     * 配置log4j参数
     */
    public static void configLog(){

        try{
            reentrantLock.lock();
            if(!init){
                logConfigurator.setFileName(NeuLogConfig.LOG_FILE_PATH);
                //设置root日志输出级别 默认为DEBUG
                logConfigurator.setRootLevel(Level.DEBUG);
                // 设置日志输出级别
                logConfigurator.setLevel("com.neucore.neulink", Level.OFF);
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
            }
        }
        finally {
            reentrantLock.unlock();
        }
    }

    public static void setLogLevel(Level level) {
        logConfigurator.setRootLevel(level);
        // 设置日志输出级别
        logConfigurator.setLevel("com.neucore.neulink", level);
    }

    public static void eTag(Class tag,Object message){
        configLog();
        Logger logger = Logger.getLogger(tag);
        logger.error(message);
    }
    public static void eTag(String tag,Object message){
        configLog();
        Logger logger = Logger.getLogger(tag);
        logger.error(message);
    }
    public static void eTag(Class tag,Object message,Throwable throwable){
        configLog();
        Logger logger = Logger.getLogger(tag);
        logger.error(message,throwable);
    }
    public static void eTag(String tag,Object message,Throwable throwable){
        configLog();
        Logger logger = Logger.getLogger(tag);
        logger.error(message,throwable);
    }

    public static void wTag(Class tag,Object message){
        configLog();
        Logger logger = Logger.getLogger(tag);
        logger.warn(message);
    }

    public static void wTag(String tag,Object message){
        configLog();
        Logger logger = Logger.getLogger(tag);
        logger.warn(message);
    }

    public static void iTag(Class tag,Object message){
        configLog();
        Logger logger = Logger.getLogger(tag);
        logger.info(message);
    }

    public static void iTag(String tag,Object message){
        configLog();
        Logger logger = Logger.getLogger(tag);
        logger.info(message);
    }

    public static void dTag(Class tag,Object message){
        configLog();
        Logger logger = Logger.getLogger(tag);
        logger.debug(message);
    }

    public static void dTag(String tag,Object message){
        configLog();
        Logger logger = Logger.getLogger(tag);
        logger.debug(message);
    }

    public static void fTag(Class tag,Object message){
        configLog();
        Logger logger = Logger.getLogger(tag);
        logger.fatal(message);
    }

    public static void fTag(String tag,Object message){
        configLog();
        Logger logger = Logger.getLogger(tag);
        logger.fatal(message);
    }

    public static void tTag(Class tag,Object message){
        configLog();
        Logger logger = Logger.getLogger(tag);
        logger.trace(message);
    }

    public static void tTag(String tag,Object message){
        configLog();
        Logger logger = Logger.getLogger(tag);
        logger.trace(message);
    }
}