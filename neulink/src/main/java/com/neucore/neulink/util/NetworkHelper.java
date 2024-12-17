package com.neucore.neulink.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.NeulinkConst;

import java.util.ArrayList;

public class NetworkHelper implements NeulinkConst {

    private static String TAG = TAG_PREFIX + "NetworkHelper";

    public interface Listener {
        void onConnectivityChange(boolean connect);
    }

    private final Context mContext;
    private ArrayList<Listener> mListeners = new ArrayList<>();
    private final ConnectivityManager mConnectivityManager;
    private int mNetworkType;

    private final BroadcastReceiver mNetworkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onConnectivityChange();
        }
    };

    private static NetworkHelper instance;

    public static NetworkHelper getInstance() {
        if (instance == null) {
            instance = new NetworkHelper();
        }
        return instance;
    }

    public NetworkHelper() {
        mContext = ContextHolder.getInstance().getContext();
        mConnectivityManager = (ConnectivityManager) mContext.getSystemService(
                Context.CONNECTIVITY_SERVICE);
    }

    public void addListener(Listener listener) {
        mListeners.add(listener);
    }

    public void reMoveListener(Listener listener) {
        mListeners.remove(listener);
    }

    public void onStart() {
        //onConnectivityChange();
        IntentFilter networkIntentFilter = new IntentFilter();
        networkIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        networkIntentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        networkIntentFilter.addAction("android.net.ethernet.ETHERNET_STATE_CHANGED");
        networkIntentFilter.addAction("android.net.ethernet.STATE_CHANGE");
        networkIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        try {
            onStop();
            mContext.registerReceiver(mNetworkReceiver, networkIntentFilter);
        } catch (Exception e) {
        }

    }

    public void onStop() {
        try {
            mContext.unregisterReceiver(mNetworkReceiver);
            //final TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE); //mContext.getSystemService(TelephonyManager.class);
        } catch (Exception e) {
        }
    }

    private void onConnectivityChange() {
        updateConnectivityStatus();
        try {
            boolean connected = getNetworkConnected();
            for (Listener listener: mListeners){
                listener.onConnectivityChange(connected);
            }
        }catch (Exception e){
            NeuLogUtils.eTag(TAG,"onConnectivityChange",e);
        }
    }

    private void updateConnectivityStatus() {
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            mNetworkType = -1;//ConnectivityManager.TYPE_NONE;
        } else {
            switch (networkInfo.getType()) {
                case ConnectivityManager.TYPE_ETHERNET:
                    mNetworkType = ConnectivityManager.TYPE_ETHERNET;
                    break;

                case ConnectivityManager.TYPE_WIFI:
                    // Determine if this is
                    // an open or secure wifi connection.
                    mNetworkType = ConnectivityManager.TYPE_WIFI;
                    break;

                case ConnectivityManager.TYPE_MOBILE:
                    mNetworkType = ConnectivityManager.TYPE_MOBILE;
                    break;

                default:
                    mNetworkType = -1;//ConnectivityManager.TYPE_NONE;
                    break;
            }
        }
    }

    public boolean getNetworkConnected() {
        return isEthernetConnected() || isCellConnected() || isWifiConnected();
    }

    private boolean isEthernetConnected() {
        return mNetworkType == ConnectivityManager.TYPE_ETHERNET;
    }

    private boolean isWifiConnected() {
        return mNetworkType == ConnectivityManager.TYPE_WIFI;
    }

    private boolean isCellConnected() {
        return mNetworkType == ConnectivityManager.TYPE_MOBILE;
    }

}