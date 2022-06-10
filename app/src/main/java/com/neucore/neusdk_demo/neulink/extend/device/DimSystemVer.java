package com.neucore.neusdk_demo.neulink.extend.device;

import android.os.Build;

import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.MacHelper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import cn.hutool.core.util.ObjectUtil;

/*
 * 获取系统自定义设备型号和版本号
 * 系统属性自定义变量 ro.product.build.dim 用于判断设备型号,客户,版本号
 * 格式为:型号_客户_版本号,如 T860_GADMEI_V1.1.1.20120101
 * android.os.Build.MODEL 则是获取通用的型号,用于系统显示用
 */
public class DimSystemVer {

    private String hardWareMode = "M6";
    public String productMode = Build.MODEL;
    public String systemVer = "v1.0.1.20120101";
    private String buildInfo = "";
    private String yekerId;   //椰壳ID
    public String deviceSn; //机器设备号

    private DimSystemVer() {
        buildInfo = getSystemProperties("ro.product.build.dim", "ics_test_v1.0.1.20130101");// android.os.SystemProperties.get("ro.product.build.dim",android.os.Build.MODEL);
        yekerId = getSystemProperties("ro.boot.cidnum", "BLB10Y2020A0404220100000009");
        deviceSn = getSystemProperties("ro.serialno", "");
        buildInfo = buildInfo.toUpperCase();
        if (buildInfo.contains("_")) {
            String[] typeInfo = buildInfo.split("_");
            if (typeInfo.length > 0) {
                hardWareMode = typeInfo[0];
            }
            if (typeInfo.length > 1) {
                productMode = typeInfo[1];
            }
//			hardWareMode="";
//			productMode=Build.MODEL.replace(" ", "_");
            if (typeInfo.length > 2) {
                systemVer = typeInfo[2];
            }
        } else {
            productMode = Build.MODEL.replace(" ", "_");
            int pos = Build.DISPLAY.lastIndexOf(".");
            if (pos < 0) pos = 0;
            systemVer = Build.DISPLAY.substring(pos);
        }
    }

    public static DimSystemVer getInstance() {
        return DimSystemVerHolder.instance;
    }

    private static class DimSystemVerHolder {
        private static final DimSystemVer instance = new DimSystemVer();
    }

    private String getSystemProperties(String key, String defValue) {
        Runtime rn = Runtime.getRuntime();
        Process p;
        String str = null;
        try {
            p = rn.exec("getprop " + key);
            InputStream in = p.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            while ((str = br.readLine()) != null) {
                if (str.startsWith("getprop")) continue;
                break;
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Excute getprop command fail!");
        }
        if (str == null || str.isEmpty()) str = defValue;
        return str;
    }

    /**
     * 获取设备硬件型号
     *
     * @return
     */
    public String getHardWareMode() {
        return hardWareMode;
    }

    /**
     * 获取设备产品型号，关联客户信息
     *
     * @return
     */
    public String getProductMode() {
        return productMode;
    }

    /**
     * 获取特定型号产品版本识别号
     *
     * @return
     */
    public String getSystemVer() {
        return systemVer;
    }

    /**
     * 获取设备yekerId
     * @return
     */
    public String getYekerId() {

        return yekerId;
    }

    /**
     * 获取clientId
     *
     * @return yekerId拼接mac地址
     */
    public String getClientId() {
        String mac = MacHelper.getEthernetMac(); //以太网mac地址
        if (ObjectUtil.isEmpty(mac)) {
            mac = MacHelper.getWifiMac(ContextHolder.getInstance().getContext());
            if (ObjectUtil.isEmpty(mac)) { //Wifi mac地址
                mac = "";
            } else {
                mac = "@" + mac.replace(":", "").toLowerCase();
            }
        }
        return getYekerId() + mac;
    }

    /**
     * 下标截取yekerId字节对应参数
     *
     * @param beginIndex 开始下标 0起始位
     * @param endIndex   结束下标 不包含
     * @return
     */
    public String getSubYekerId(int beginIndex, int endIndex) {
        String yekerId = getYekerId();
        if (ObjectUtil.isEmpty(yekerId)) {
            return null;
        }
        try {
            return yekerId.substring(beginIndex, endIndex);
        } catch (StringIndexOutOfBoundsException e) {
            return null;
        }
    }

}
