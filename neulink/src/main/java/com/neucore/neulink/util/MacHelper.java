package com.neucore.neulink.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.NeulinkConst;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static android.os.Build.VERSION.SDK_INT;

public class MacHelper implements NeulinkConst{

    private static String TAG = TAG_PREFIX+"MacHelper";


    /**
     * 获取MAC地址
     * @return MAC地址
     */
    public static String getWifiMac(Context context) {
        String mac = "";
        if (SDK_INT <= Build.VERSION_CODES.KITKAT) {
            mac = getMacFromWifiManager(context);
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
                NeuLogUtils.dTag(TAG, "getMacFromHardware wifiMac= " + wifiMac);
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
            Map<String,String> result = ShellExecutor.execute(ContextHolder.getInstance().getContext(),"cat /sys/class/net/wlan0/address");
            String success = result.get("success");
            if("1".equals(success)){
                //读取CPU信息
                String stdout = result.get("stdout");
                InputStreamReader ir = new InputStreamReader(new BufferedInputStream(new ByteArrayInputStream(stdout.getBytes(StandardCharsets.UTF_8))));
                LineNumberReader input = new LineNumberReader(ir);

                for (; null != str;) {
                    str = input.readLine();
                    if (str != null) {
                        macSerial = str.trim();// 去空格
                        NeuLogUtils.dTag(TAG, "getMacFromInterface wifiMac= " + macSerial);
                        break;
                    }
                }
                input.close();
            }
        } catch (Exception ex) {
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
            Map<String,String> result = ShellExecutor.execute(ContextHolder.getInstance().getContext(),"ifconfig "+name);
            String success = result.get("success");
            if("1".equals(success)){
                //读取CPU信息
                String stdout = result.get("stdout");
                InputStreamReader ir = new InputStreamReader(new BufferedInputStream(new ByteArrayInputStream(stdout.getBytes(StandardCharsets.UTF_8))));
                LineNumberReader input = new LineNumberReader(ir);
                String readLine = "";

                for (; null != readLine;) {
                    readLine = input.readLine();
                    //NeuLogUtils.dTag(TAG, "readLine= " + readLine);
                    if (readLine != null && readLine.contains("HWaddr")) {
                        String macSerial = readLine.trim();// 去空格

                        if(macSerial != null){
                            macSerial = macSerial.substring(macSerial.indexOf("HWaddr"), macSerial.length());
                            String[] values = macSerial.split(" ");
                            for (String str: values){
                                if (str != null && !str.isEmpty() && str.contains(":")){
                                    wifiMac = str;
                                    NeuLogUtils.dTag(TAG, "getMacFromIfconfig wifiMac= " + wifiMac);
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
                input.close();
            }
        } catch (Exception ex) {
        }
        return wifiMac;
    }


    /**
     * 获取5.0以下的MAC地址
     *
     * @return MAC地址
     */
    public static String getMacFromWifiManager(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(
                Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }


    public static String getEthernetMac() {

        try {
            Map<String,String> result = ShellExecutor.execute(ContextHolder.getInstance().getContext(),"ifconfig eth0");
            String success = result.get("success");
            if("1".equals(success)){
                //读取CPU信息
                String stdout = result.get("stdout");
                InputStreamReader ir = new InputStreamReader(new BufferedInputStream(new ByteArrayInputStream(stdout.getBytes(StandardCharsets.UTF_8))));
                LineNumberReader input = new LineNumberReader(ir);
                String readLine = "";

                String mac = "";

                for (; null != readLine;) {
                    readLine = input.readLine();
                    //NeuLogUtils.dTag(TAG, "readLine= " + readLine);
                    if (readLine != null ) {
                        String infoStr = readLine.trim();
                        NeuLogUtils.dTag(TAG, "getInfoFromIfconfig infoStr= " + infoStr);
                        if(readLine.contains("HWaddr")){

                            if(infoStr != null){
                                infoStr = infoStr.substring(infoStr.indexOf("HWaddr"), infoStr.length());
                                String[] values = infoStr.split(" ");
                                for (String str: values){
                                    if (str != null && !str.isEmpty() && str.contains(":")){
                                        mac = str;
                                        NeuLogUtils.dTag(TAG, "getEthernetMac Mac= " + mac);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                input.close();
                return mac;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String[] getEthernetInfoFromIfconfig() {

        try {
            Map<String,String> result = ShellExecutor.execute(ContextHolder.getInstance().getContext(),"ifconfig eth0");
            String success = result.get("success");
            if("1".equals(success)){
                //读取CPU信息
                String stdout = result.get("stdout");
                InputStreamReader ir = new InputStreamReader(new BufferedInputStream(new ByteArrayInputStream(stdout.getBytes(StandardCharsets.UTF_8))));
                LineNumberReader input = new LineNumberReader(ir);
                String readLine = "";

                String mac = "";
                String ip = "";
                String ipv6 = "";
                String bcast = "";
                String mask = "";

                for (; null != readLine;) {
                    readLine = input.readLine();
                    //NeuLogUtils.dTag(TAG, "readLine= " + readLine);
                    if (readLine != null ) {
                        String infoStr = readLine.trim();
                        NeuLogUtils.dTag(TAG, "getInfoFromIfconfig infoStr= " + infoStr);
                        if(readLine.contains("HWaddr")){

                            if(infoStr != null){
                                infoStr = infoStr.substring(infoStr.indexOf("HWaddr"), infoStr.length());
                                String[] values = infoStr.split(" ");
                                for (String str: values){
                                    if (str != null && !str.isEmpty() && str.contains(":")){
                                        mac = str;
                                        NeuLogUtils.dTag(TAG, "getInfoFromIfconfig wifiMac= " + mac);
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
                                        NeuLogUtils.dTag(TAG, "getInfoFromIfconfig ip= " + ip);
                                    }else if (str != null && !str.isEmpty() && str.contains("Bcast")){
                                        String[] bcastStr = str.split(":");
                                        bcast = bcastStr[1];
                                        NeuLogUtils.dTag(TAG, "getInfoFromIfconfig bcast= " + bcast);
                                    }else if (str != null && !str.isEmpty() && str.contains("Mask")){
                                        String[] maskStr = str.split(":");
                                        mask = maskStr[1];
                                        NeuLogUtils.dTag(TAG, "getInfoFromIfconfig mask= " + mask);
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
                                        NeuLogUtils.dTag(TAG, "getInfoFromIfconfig ipv6= " + ipv6);
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
                input.close();
                return new String[]{mac, ip, bcast, mask, ipv6};
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}