package com.neucore.neusdk_demo.neulink.extend;

import android.os.Build;

import com.blankj.utilcode.util.AppUtils;
import com.neucore.neulink.IDeviceExtendInfoCallback;
import com.neucore.neulink.impl.cmd.msg.SoftVInfo;
import com.neucore.neulink.impl.cmd.msg.SubApp;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;
import com.neucore.neulink.util.MacHelper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.ObjectUtil;

/**
 * 设备扩展信息获取回调
 */
public class MyDeviceExtendInfoCallBack implements IDeviceExtendInfoCallback {

    /**
     * 子应用列表
     *
     * @return
     */
    @Override
    public List<SubApp> getSubApps() {
        List<SubApp> lis = new ArrayList<>();
        //日历
        SubApp rl = new SubApp();
        rl.setReportName("com.gadmei.perpetualcalendar");
        rl.setReportVersion(Integer.toString(AppUtils.getAppVersionCode("com.gadmei.perpetualcalendar")));
        lis.add(rl);
        //天气
        SubApp Weather = new SubApp();
        Weather.setReportName("com.yekertech.dpfweather");
        Weather.setReportVersion(Integer.toString(AppUtils.getAppVersionCode("com.yekertech.dpfweather")));
        lis.add(Weather);
        //音乐
        SubApp music = new SubApp();
        music.setReportName("com.bestidear.qiymusic");
        music.setReportVersion(Integer.toString(AppUtils.getAppVersionCode("com.bestidear.qiymusic")));
        lis.add(music);

        return lis;
    }

    /**
     * 扩展属性
     */
    @Override
    public List<Map<String, String>> getAttrs() {
        return null;
    }

    /**
     * 产品型号
     */
    @Override
    public String getModel() {
        return DimSystemVer.getInstance().getProductMode();
    }

    /**
     * 移动设备国际身份码 可空
     *
     * @return
     */
    @Override
    public String getImei() {
        return null;
    }

    /**
     * 移动用户国际识别码 可控
     *
     * @return
     */
    @Override
    public String getImsi() {
        return null;
    }

    /**
     * SIM卡卡号 可空
     *
     * @return
     */
    @Override
    public String getIccid() {
        return null;
    }

    /**
     * 设备所在经度
     */
    @Override
    public String getLat() {
        return null;
    }

    /**
     * 设备所在纬度 可空
     */
    @Override
    public String getLng() {
        return null;
    }

    /**
     * 以太网网卡名称
     */
    @Override
    public String getInterface() {
        return null;
    }

    @Override
    public String getCpuModel() {
        return DimSystemVer.getInstance().getSubYekerId(6, 8);
    }

    /**
     * wifi模组型号
     */
    @Override
    public String getWifiModel() {
        return DimSystemVer.getInstance().getSubYekerId(8, 10);
    }

    /**
     * npu型号
     */
    @Override
    public String getNpuModel() {
//        return DeviceUtils.getNpuMode(ContextHolder.getInstance().getContext());
        return null;
    }

    /**
     * 设备软件版本号
     *
     * @return
     */
    @Override
    public SoftVInfo getMain() {
        SoftVInfo softVInfo = new SoftVInfo();
        softVInfo.setReportName(AppUtils.getAppName()); //主app名字尊从公司统一命名规则
        softVInfo.setReportVersion(AppUtils.getAppVersionName()); //主app版本
        softVInfo.setOsVersion(String.valueOf(Build.VERSION.SDK_INT)); //操作系统版本
        softVInfo.setOsName(DeviceUtils.getOsVersion()); //操作系统名称
        softVInfo.setFirVersion(DimSystemVer.getInstance().getSystemVer()); //固件版本
        softVInfo.setFirName(DimSystemVer.getInstance().getSystemVer()); //固件名称
        return softVInfo;
    }

    /**
     * 屏尺寸 可空
     *
     * @return
     */
    @Override
    public String getScreenSize() {
        return DimSystemVer.getInstance().getSubYekerId(10, 13);
    }

    /**
     * 屏接口
     *
     * @return
     */
    @Override
    public String getScreenInterface() {
        return DimSystemVer.getInstance().getSubYekerId(13, 14);
    }

    /**
     * 屏分辨率
     *
     * @return
     */
    @Override
    public String getScreenResolution() {
        return DimSystemVer.getInstance().getSubYekerId(14, 16);
        //String screenResolution = ScreenUtils.getScreenWidth() + "x" + ScreenUtils.getScreenHeight();
    }

    /**
     * 批次号 年后两位+两位序号
     *
     * @return
     */
    @Override
    public String getBno() {
        return DimSystemVer.getInstance().getSubYekerId(16, 20);
    }

    /*
     * 获取系统自定义设备型号和版本号
     * 系统属性自定义变量 ro.product.build.dim 用于判断设备型号,客户,版本号
     * 格式为:型号_客户_版本号,如 T860_GADMEI_V1.1.1.20120101
     * android.os.Build.MODEL 则是获取通用的型号,用于系统显示用
     */
    public static class DimSystemVer {

        private String hardWareMode = "M6";
        public String productMode = Build.MODEL;
        public String systemVer = "v1.0.1.20120101";
        private String buildInfo = "";
        private String productKey;   //椰壳ID
        private String yekerId;   //椰壳ID
        private String deviceSecret;//设备密钥
        public String deviceSn; //机器设备号

        private DimSystemVer() {
            buildInfo = getSystemProperties("ro.product.build.dim", "ics_test_v1.0.1.20130101");// android.os.SystemProperties.get("ro.product.build.dim",android.os.Build.MODEL);
            productKey = getSystemProperties("ro.boot.product", "");
            yekerId = getSystemProperties("ro.boot.cidnum", "BLB10Y2020A0404220100000009");
            deviceSecret = getSystemProperties("ro.boot.secret", "");
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
         * 获取授权设备所属产品Id
         * @return
         */
        public String getProductKey() {
            return productKey;
        }

        /**
         * 获取设备yekerId
         * @return
         */
        public String getYekerId() {

            return yekerId;
        }

        public String getDeviceSecret() {

            return deviceSecret;
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
}