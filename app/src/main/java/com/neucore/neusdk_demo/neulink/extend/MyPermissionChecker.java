package com.neucore.neusdk_demo.neulink.extend;

import android.Manifest;

import com.neucore.neulink.IPermissionChecker;
import com.neucore.neulink.impl.cmd.cfg.ConfigContext;
import com.neucore.neulink.util.ContextHolder;
import com.neucore.neulink.util.DeviceUtils;

import java.util.Arrays;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * 相关权限检测
 */
public class MyPermissionChecker implements IPermissionChecker {

    public String[] STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public String[] PERMISSIONS;

    public <T> T[] concatAll(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    @Override
    public boolean has() {

        int storeType = DeviceUtils.getStoreType();

        if(storeType==DeviceUtils.SDCARD_TYPE){
            PERMISSIONS = concatAll(STORAGE);
        }

        return EasyPermissions.hasPermissions(ContextHolder.getInstance().getContext(), PERMISSIONS);
    }
}
