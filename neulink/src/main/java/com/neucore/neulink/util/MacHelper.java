package com.neucore.neulink.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import com.neucore.neulink.app.NeulinkConst;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import static android.os.Build.VERSION.SDK_INT;

public class MacHelper {

    private static String TAG = NeulinkConst.TAG_PREFIX+"MacHelper";


    /**
     * 获取MAC地址
     * @return MAC地址
     */
    public static String getWifiMac(Context context) {
        String mac = "";
        if (SDK_INT <= Build.VERSION_CODES.KITKAT) {
            mac = getMacAddress(context);
        } else {
            // adb shell ifconfig wlan0
            mac = getMacFromIfconfig();
            if(mac == null){
                // adb shell cat /sys/class/net/wlan0/address
                mac = getMacFromInterface();
            }
            if(mac == null){
                mac = getMacFromHardware();
            }
        }
        return mac;
    }

    private static String getMacFromHardware() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return null;
                }
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                String wifiMac = res1 != null ? res1.toString() : null;
                Log.d(TAG, "getMacFromHardware wifiMac= " + wifiMac);
                return wifiMac;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取5.0以上的MAC地址
     *
     * @return MAC地址
     */
    public static String getMacFromInterface() {
        String str = "";
        String macSerial = "";
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str;) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    Log.d(TAG, "getMacFromInterface wifiMac= " + macSerial);
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return macSerial;
    }

    private static String getMacFromIfconfig() {

        String wifiMac = ifconfig("wlan0");

        if(wifiMac==null||wifiMac.trim().length()==0){
            wifiMac = ifconfig("eth0");
        }
        return wifiMac;
    }

    private static String ifconfig(String name){
        String wifiMac = null;
        try {
            Process pp = Runtime.getRuntime().exec("ifconfig "+name);
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String readLine = "";

            for (; null != readLine;) {
                readLine = input.readLine();
                //Log.d(TAG, "readLine= " + readLine);
                if (readLine != null && readLine.contains("HWaddr")) {
                    String macSerial = readLine.trim();// 去空格

                    if(macSerial != null){
                        macSerial = macSerial.substring(macSerial.indexOf("HWaddr"), macSerial.length());
                        String[] values = macSerial.split(" ");
                        for (String str: values){
                            if (str != null && !str.isEmpty() && str.contains(":")){
                                wifiMac = str;
                                Log.d(TAG, "getMacFromIfconfig wifiMac= " + wifiMac);
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return wifiMac;
    }


    /**
     * 获取5.0以下的MAC地址
     *
     * @return MAC地址
     */
    public static String getMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(
                Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }


    public static String getEthernetMac() {

        try {
            Process pp = Runtime.getRuntime().exec("ifconfig eth0");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String readLine = "";

            String mac = "";

            for (; null != readLine;) {
                readLine = input.readLine();
                //Log.d(TAG, "readLine= " + readLine);
                if (readLine != null ) {
                    String infoStr = readLine.trim();
                    Log.d(TAG, "getInfoFromIfconfig infoStr= " + infoStr);
                    if(readLine.contains("HWaddr")){

                        if(infoStr != null){
                            infoStr = infoStr.substring(infoStr.indexOf("HWaddr"), infoStr.length());
                            String[] values = infoStr.split(" ");
                            for (String str: values){
                                if (str != null && !str.isEmpty() && str.contains(":")){
                                    mac = str;
                                    Log.d(TAG, "getEthernetMac Mac= " + mac);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            return mac;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String[] getEthernetInfoFromIfconfig() {

        try {
            Process pp = Runtime.getRuntime().exec("ifconfig eth0");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String readLine = "";

            String mac = "";
            String ip = "";
            String ipv6 = "";
            String bcast = "";
            String mask = "";

            for (; null != readLine;) {
                readLine = input.readLine();
                //Log.d(TAG, "readLine= " + readLine);
                if (readLine != null ) {
                    String infoStr = readLine.trim();
                    Log.d(TAG, "getInfoFromIfconfig infoStr= " + infoStr);
                    if(readLine.contains("HWaddr")){

                        if(infoStr != null){
                            infoStr = infoStr.substring(infoStr.indexOf("HWaddr"), infoStr.length());
                            String[] values = infoStr.split(" ");
                            for (String str: values){
                                if (str != null && !str.isEmpty() && str.contains(":")){
                                    mac = str;
                                    Log.d(TAG, "getInfoFromIfconfig wifiMac= " + mac);
                                    break;
                                }
                            }
                        }
                    }else if(infoStr.contains("inet addr")){
                        if(infoStr != null){
                            infoStr = infoStr.replace("inet addr", "inetaddr");
                            String[] values = infoStr.split(" ");
                            for (String str: values){
                                if (str != null && !str.isEmpty() && str.contains("inetaddr")){
                                    String[] ipStr = str.split(":");
                                    ip = ipStr[1];
                                    Log.d(TAG, "getInfoFromIfconfig ip= " + ip);
                                }else if (str != null && !str.isEmpty() && str.contains("Bcast")){
                                    String[] bcastStr = str.split(":");
                                    bcast = bcastStr[1];
                                    Log.d(TAG, "getInfoFromIfconfig bcast= " + bcast);
                                }else if (str != null && !str.isEmpty() && str.contains("Mask")){
                                    String[] maskStr = str.split(":");
                                    mask = maskStr[1];
                                    Log.d(TAG, "getInfoFromIfconfig mask= " + mask);
                                }
                            }
                        }

                    }else if(infoStr.contains("inet6 addr")){
                        if(infoStr != null){
                            infoStr = infoStr.replace("inet6 addr", "inet6addr");
                            String[] values = infoStr.split(" ");
                            for (String str: values){
                                if (str != null && !str.isEmpty() && str.contains("::")){
                                    ipv6 = str;
                                    Log.d(TAG, "getInfoFromIfconfig ipv6= " + ipv6);
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
            }

            return new String[]{mac, ip, bcast, mask, ipv6};
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}