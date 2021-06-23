package com.neucore.neulink.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.neucore.neulink.IOnNetStatusListener;

public class NetBroadcastReceiver extends BroadcastReceiver {
    //filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
    public static IOnNetStatusListener onNetListener;
    private static long oldTime;
    /**
     * -1 NoNetWork; 0 TYPE_MOBILE; 1 TYPE_WIFI; 2 TYPE_ETHERNET; 3 OtherNetWork
     */
    public static int netType;
    public static String netName;

    public static void setOnNetListener(IOnNetStatusListener listener) {
        onNetListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        long time = System.currentTimeMillis();
        if (time - oldTime < 1000) {
            return;
        }
        oldTime = time;
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isAvailable()) {
            switch (netInfo.getType()) {
                case ConnectivityManager.TYPE_MOBILE:
                    netType = 0;
                    netName = netInfo.getSubtypeName();
                    break;
                case ConnectivityManager.TYPE_WIFI:
                    netType = 1;
                    netName = netInfo.getTypeName();
                    break;
                case ConnectivityManager.TYPE_ETHERNET:
                    netType = 2;
                    netName = netInfo.getTypeName();
                    break;
                default:
                    netType = 3;
                    netName = "OtherNetWork";
                    break;
            }
        } else {
            netType = -1;
            netName = "NoNetWork";
        }
        if (onNetListener != null) {
            onNetListener.onNetStatus(netType, netName);
        }
    }
}
