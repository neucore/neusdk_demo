package com.neucore.neusdk_demo.app;

import android.app.Application;
import android.content.Context;

import com.neucore.neusdk_demo.neulink.MyInstaller;

public class MyApplication extends Application
{
    private static MyApplication instance ;
    private String TAG = "MyApplication";
    public static MyApplication getInstance(){
        return instance;
    }
    @Override
    public void onCreate()
    {
        super.onCreate();
        instance=this;
    }

    public static Context getContext(){
        return instance.getApplicationContext();
    }

    private static int threadAlive = 0;

    public static int getThreadAlive() {
        return threadAlive;
    }

    public static void setThreadAlive(int alive) {
        MyApplication.threadAlive = alive;
    }

    /**
     * onPermissionsGranted之后调用
     */
    public static void installSDK(){
        MyInstaller.getInstance().install(instance);
    }
}
