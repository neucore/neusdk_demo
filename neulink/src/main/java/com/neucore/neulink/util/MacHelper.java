package com.neucore.neulink.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import com.neucore.neulink.NeulinkConst;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.os.Build.VERSION.SDK_INT;

import cn.hutool.core.util.ObjectUtil;

public class MacHelper implements NeulinkConst{

    private static String TAG = TAG_PREFIX+"MacHelper";


    /**
     * 获取MAC地址
     * @return MAC地址
     * @deprecated replace by getMacAddress();
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
                mac = getWlan0MacFromInterface();
            }
            if(mac == null){
                mac = getWlan0MacFromHardware();
            }
        }
        return mac;
    }

    /**
     * 获取mac地址
     * 优先返回有线以太网mac地址、如果为空则，则返回Wi-Fi mac地址
     * @param context
     * @return
     */
    public static String getMacAddress(Context context) {
        String macAddress = getEthernetMac(); //以太网mac地址
        if (ObjectUtil.isEmpty(macAddress)) {
            macAddress = getWifiMac(ContextHolder.getInstance().getContext());
        }
        return macAddress;
    }

    private static String wlan0MacFromHardware;
    private static String getWlan0MacFromHardware() {
        if(ObjectUtil.isEmpty(wlan0MacFromHardware)){
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
                    wlan0MacFromHardware = wifiMac;
                    return wlan0MacFromHardware;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return wlan0MacFromHardware;
    }


    /**
     * 获取5.0以上的MAC地址
     *
     * @return MAC地址
     */
    private static String wlan0MacFromInterface;
    private static String getWlan0MacFromInterface() {
        if(ObjectUtil.isEmpty(wlan0MacFromInterface)){
            String str = "";
            String macSerial = "";
            LineNumberReader input = null;
            try {
                Map<String,String> result = ShellExecutor.execute(ContextHolder.getInstance().getContext(),"cat /sys/class/net/wlan0/address");
                String success = result.get("success");
                if("1".equals(success)){
                    //读取CPU信息
                    String stdout = result.get("stdout");
                    InputStreamReader ir = new InputStreamReader(new BufferedInputStream(new ByteArrayInputStream(stdout.getBytes(StandardCharsets.UTF_8))));
                    input = new LineNumberReader(ir);

                    for (; null != str;) {
                        str = input.readLine();
                        if (str != null) {
                            macSerial = str.trim();// 去空格
                            break;
                        }
                    }

                }
                wlan0MacFromInterface = macSerial;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            finally {
                try {
                    if(ObjectUtil.isNotEmpty(input)) {
                        input.close();
                    }
                } catch (IOException e) {
                }
            }
        }

        return wlan0MacFromInterface;
    }

    /**
     * 有线网络优先
     * @return
     */
    private static String getMacFromIfconfig() {

        String wifiMac = ifconfig("eth0");

        if(wifiMac==null||wifiMac.trim().length()==0){
            wifiMac = ifconfig("wlan0");
        }

        return wifiMac;
    }
    private static Map<String,String> ifconfigMap = new HashMap<>();
    private static String ifconfig(String name){
        String nameIfconfig = ifconfigMap.get(name);
        if(ObjectUtil.isEmpty(nameIfconfig)){
            String wifiMac = null;
            LineNumberReader input = null;
            try {
                Map<String,String> result = ShellExecutor.execute(ContextHolder.getInstance().getContext(),"ifconfig "+name);
                String success = result.get("success");
                if("1".equals(success)){
                    //读取CPU信息
                    String stdout = result.get("stdout");
                    InputStreamReader ir = new InputStreamReader(new BufferedInputStream(new ByteArrayInputStream(stdout.getBytes(StandardCharsets.UTF_8))));
                    input = new LineNumberReader(ir);
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
                                        break;
                                    }
                                }
                                if(ObjectUtil.isNotEmpty(wifiMac)){
                                    break;
                                }
                            }
                        }
                    }
                }
                nameIfconfig = wifiMac;
                ifconfigMap.put(name,wifiMac);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            finally {
                try {
                    if(ObjectUtil.isNotEmpty(input)) {
                        input.close();
                    }
                } catch (IOException e) {
                }
            }
        }
        return nameIfconfig;
    }


    /**
     * 获取5.0以下的MAC地址
     *
     * @return MAC地址
     */
    private static String getMacFromWifiManager(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(
                Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * 获取以太网mac地址
     * @return
     */
    private static String ethernetMac;
    public static String getEthernetMac() {
        if(ObjectUtil.isEmpty(ethernetMac)){
            LineNumberReader input = null;
            try {
                Map<String,String> result = ShellExecutor.execute(ContextHolder.getInstance().getContext(),"ifconfig eth0");
                String success = result.get("success");
                if("1".equals(success)){
                    //读取CPU信息
                    String stdout = result.get("stdout");
                    InputStreamReader ir = new InputStreamReader(new BufferedInputStream(new ByteArrayInputStream(stdout.getBytes(StandardCharsets.UTF_8))));
                    input = new LineNumberReader(ir);
                    String readLine = "";

                    String mac = "";

                    for (; null != readLine;) {
                        readLine = input.readLine();
                        //NeuLogUtils.dTag(TAG, "readLine= " + readLine);
                        if (readLine != null ) {
                            String infoStr = readLine.trim();
                            if(readLine.contains("HWaddr")){
                                if(infoStr != null){
                                    infoStr = infoStr.substring(infoStr.indexOf("HWaddr"), infoStr.length());
                                    String[] values = infoStr.split(" ");
                                    for (String str: values){
                                        if (str != null && !str.isEmpty() && str.contains(":")){
                                            mac = str;
                                            break;
                                        }
                                    }
                                    if(ObjectUtil.isNotEmpty(mac)){
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    ethernetMac = mac;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            finally {
                if(ObjectUtil.isNotEmpty(input)){
                    try {
                        input.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        return ethernetMac;
    }
    private static String[] ethernetInfoFromIfconfig;
    public static String[] getEthernetInfoFromIfconfig() {
        LineNumberReader input = null;
        if(ObjectUtil.isEmpty(ethernetInfoFromIfconfig)){
            try {
                Map<String,String> result = ShellExecutor.execute(ContextHolder.getInstance().getContext(),"ifconfig eth0");
                String success = result.get("success");
                if("1".equals(success)){
                    //读取CPU信息
                    String stdout = result.get("stdout");
                    InputStreamReader ir = new InputStreamReader(new BufferedInputStream(new ByteArrayInputStream(stdout.getBytes(StandardCharsets.UTF_8))));
                    input = new LineNumberReader(ir);
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
                            if(readLine.contains("HWaddr")){
                                if(infoStr != null){
                                    infoStr = infoStr.substring(infoStr.indexOf("HWaddr"), infoStr.length());
                                    String[] values = infoStr.split(" ");
                                    for (String str: values){
                                        if (str != null && !str.isEmpty() && str.contains(":")){
                                            mac = str;
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
                                        }else if (str != null && !str.isEmpty() && str.contains("Bcast")){
                                            String[] bcastStr = str.split(":");
                                            bcast = bcastStr[1];
                                        }else if (str != null && !str.isEmpty() && str.contains("Mask")){
                                            String[] maskStr = str.split(":");
                                            mask = maskStr[1];
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
                                            break;
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }

                    ethernetInfoFromIfconfig = new String[]{mac, ip, bcast, mask, ipv6};
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            finally {
                if(ObjectUtil.isNotEmpty(input)) {
                    try {
                        input.close();
                    } catch (IOException e) {
                    }
                }
            }
        }

        return ethernetInfoFromIfconfig;
    }
}