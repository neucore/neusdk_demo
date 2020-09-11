package com.neucore.neulink.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MemoryUtils {
    private static String TAG = "MemoryUtils";
    /** 内存信息文件的路径 */
    public static final String MEMORY_INFO_PATH = "/proc/meminfo";
    /** buffer size */
    public static final int BUFFER_SIZE = 8192;
    /** K B*/
    public static final long KB = 1024;
    /**
     * 工具类，构造方法私有
     */
    private MemoryUtils() {

    }

    /**
     * read the total memory (kb) of certain device
     *
     * @return total memory (KB) of device
     */
    public static long getTotalMemory() {
        long memory = 0;
        try {
            String line = "";
            String memTotal = "";
            BufferedReader localBufferedReader = new BufferedReader(new FileReader(MEMORY_INFO_PATH), BUFFER_SIZE);
            while ((line = localBufferedReader.readLine()) != null) {
                if (line.contains("MemTotal")) {
                    String[] total = line.split(":");
                    memTotal = total[1].trim();
                    break;
                }
            }
            localBufferedReader.close();
            String[] memKb = memTotal.split(" ");
            memTotal = memKb[0].trim();
            memory = Long.parseLong(memTotal);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return memory;
    }

    /**
     * get free memory
     *
     * @param context Context
     * @return free memory of device
     */
    public static long getFreeMemorySize(Context context) {
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        am.getMemoryInfo(outInfo);
        long avaliMem = outInfo.availMem;
        return avaliMem / KB;
    }

    /**
     * get the memory of process with certain pid
     *
     * @param pid
     *            pid of process
     * @param context
     *            context of certain activity
     * @return memory usage of certain process
     */
    public static int getPidMemorySize(int pid, Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int[] myMempid = new int[] { pid };
        Debug.MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(myMempid);
        memoryInfo[0].getTotalSharedDirty();
        // int memSize = memoryInfo[0].dalvikPrivateDirty;
        // int memSize = memoryInfo[0].getTotalPrivateDirty();
        int memSize = memoryInfo[0].getTotalPss();
        return memSize;
    }
}
