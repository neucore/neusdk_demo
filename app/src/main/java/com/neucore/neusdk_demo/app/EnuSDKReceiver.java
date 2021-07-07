package com.neucore.neusdk_demo.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neusdk_demo.MenuActivity;

public class EnuSDKReceiver extends BroadcastReceiver {
    private String TAG = "EnuSDKReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent i = new Intent(context, MenuActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            String npuVersion = DeviceUtils.getNpuMode(context);
            Log.i(TAG,"NPUVersion: "+ npuVersion);
        }
    }
}
