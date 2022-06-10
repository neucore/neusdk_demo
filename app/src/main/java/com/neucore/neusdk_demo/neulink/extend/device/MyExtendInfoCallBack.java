package com.neucore.neusdk_demo.neulink.extend.device;

import android.os.Build;

import com.blankj.utilcode.util.AppUtils;
import com.neucore.neulink.IDeviceExtendInfoCallback;
import com.neucore.neulink.impl.cmd.msg.SoftVInfo;
import com.neucore.neulink.impl.cmd.msg.SubApp;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyExtendInfoCallBack implements IDeviceExtendInfoCallback {

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
        return DeviceUtils.getNpuMode(ContextHolder.getInstance().getContext());
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

}