package com.neucore.neusdk_demo.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;



import com.neucore.neusdk_demo.MenuActivity;

public class EnuSDKReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent i = new Intent(context, MenuActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
