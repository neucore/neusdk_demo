package com.neucore.neulink.util;

import android.content.Context;
import android.util.Log;

import com.neucore.neulink.NeulinkConst;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;


public class CpuStat implements NeulinkConst {

    private static String TAG = TAG_PREFIX+"CpuStat";

    /** CPU信息文件的路径 */
    public static final String CPUC_INFO_PATH = "/proc/cpuinfo";
    /** 状态信息文件的路径 */
    public static final String STAT_PATH = "/proc/stat";

    /** Context */
    private static Context mContext;

    private static long mProcessCpu;
    private static long mIdleCpu;
    private static long mTotalCpu;

    private static long mProcessCpu2;
    private static long mIdleCpu2;
    private static long mTotalCpu2;

    private static boolean mIsInitialStatics = true;
    private static SimpleDateFormat sFormatterFile = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static long mTotalMemorySize;

    private static long mInitialTraffic;
    private static long mLastestTraffic;
    private static long mTraffic;

    private static String mProcessCpuRatio = "";
    private static String mTotalCpuRatio = "";

    private static int mPid;
    private static String mUid;

    public CpuStat(Context context, int pid, String uid) {
        mContext = context;
        mPid = pid;
        mUid = uid;
        mTotalMemorySize = MemoryUtils.getTotalMemory();
    }

    /**
     * read the status of CPU
     *
     * @throws FileNotFoundException
     */
    private void readCpuStat() {
        String processPid = Integer.toString(mPid);
        String cpuStatPath = "/proc/" + processPid + "/stat";
        try {
            // monitor cpu stat of certain process
            RandomAccessFile processCpuInfo = new RandomAccessFile(cpuStatPath, "r");
            String line = "";
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.setLength(0);
            while ((line = processCpuInfo.readLine()) != null) {
                stringBuffer.append(line + "\n");
            }
            String[] tok = stringBuffer.toString().split(" ");
            mProcessCpu = Long.parseLong(tok[13]) + Long.parseLong(tok[14]);
            processCpuInfo.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            // monitor total and idle cpu stat of certain process
            RandomAccessFile cpuInfo = new RandomAccessFile(STAT_PATH, "r");
            String[] toks = cpuInfo.readLine().split(" ");
            mIdleCpu = Long.parseLong(toks[5]);
            mTotalCpu = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                    + Long.parseLong(toks[5]) + Long.parseLong(toks[6]) + Long.parseLong(toks[7])
                    + Long.parseLong(toks[8]);
            cpuInfo.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * get CPU name
     *
     * @return CPU name
     */
    public static String getCpuName() {
        try {
            RandomAccessFile cpu_stat = new RandomAccessFile(CPUC_INFO_PATH, "r");
            String[] cpu = cpu_stat.readLine().split(":"); // cpu信息的前一段是含有processor字符串，此处替换为不显示
            cpu_stat.close();
            return cpu[1];
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return "";
    }

    /**
     * reserve used ratio of process CPU and total CPU, meanwhile collect
     * network mTraffic
     *
     * @return network mTraffic ,used ratio of process CPU and total CPU in
     *         certain interval
     */
    public ArrayList<String> getCpuRatioInfo() {
        DecimalFormat fomart = new DecimalFormat();
        fomart.setMaximumFractionDigits(2);
        fomart.setMinimumFractionDigits(2);

        readCpuStat();
        ArrayList<String> cpuUsedRatio = new ArrayList<String>();
        try {
            Calendar cal = Calendar.getInstance();
            String mDateTime2 = sFormatterFile.format(cal.getTime().getTime() + 8 * 60 * 60 * 1000);

            if (mIsInitialStatics == true) {
                mInitialTraffic = TrafficUtils.getTrafficInfo(mUid);
                mIsInitialStatics = false;
            } else {
                mLastestTraffic = TrafficUtils.getTrafficInfo(mUid);
                if (mInitialTraffic == -1) {
                    mTraffic = -1;
                } else {
                    mTraffic = (mLastestTraffic - mInitialTraffic + 1023) / MemoryUtils.KB;
                }
                mProcessCpuRatio = fomart.format(100 * ((double) (mProcessCpu - mProcessCpu2) / (double) (mTotalCpu - mTotalCpu2)));
                mTotalCpuRatio = fomart.format(100 * ((double) ((mTotalCpu - mIdleCpu) - (mTotalCpu2 - mIdleCpu2)) / (double) (mTotalCpu - mTotalCpu2)));

                long pidMemory = MemoryUtils.getPidMemorySize(mPid, mContext);
                String pMemory = fomart.format((double) pidMemory / MemoryUtils.KB);

                long freeMemory = MemoryUtils.getFreeMemorySize(mContext);
                String fMemory = fomart.format((double) freeMemory / MemoryUtils.KB);

                String percent = "统计出错";
                if (mTotalMemorySize != 0) {
                    percent = fomart.format(((double) pidMemory / (double) mTotalMemorySize) * 100);
                }

                // whether certain device supports mTraffic statics
                /*if (mTraffic == -1) {
                    MonitorService.bw.write(mDateTime2 + "," + pMemory + "," + percent + "," + fMemory + "," + mProcessCpuRatio + "," + mTotalCpuRatio + "," + "本程序或本设备不支持流量统计" + "\r\n");
                } else {
                    MonitorService.bw.write(mDateTime2 + "," + pMemory + "," + percent + "," + fMemory + "," + mProcessCpuRatio + "," + mTotalCpuRatio + "," + mTraffic + "\r\n");
                }*/
            }
            mTotalCpu2 = mTotalCpu;
            mProcessCpu2 = mProcessCpu;
            mIdleCpu2 = mIdleCpu;
            cpuUsedRatio.add(mProcessCpuRatio);
            cpuUsedRatio.add(mTotalCpuRatio);
            cpuUsedRatio.add(String.valueOf(mTraffic));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return cpuUsedRatio;
    }

    public static float getCpuUsed() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();
            String[] toks = load.split(" ");
            long idle1 = Long.parseLong(toks[5]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
            try {
                Thread.sleep(360);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
            reader.seek(0);
            load = reader.readLine();
            reader.close();
            toks = load.split(" ");
            long idle2 = Long.parseLong(toks[5]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
            return (float) (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public static String getCpuTemp() {
        String temp = "Unknow";
        BufferedReader br = null;
        FileReader fr = null;
        try {
            File dir = new File("/sys/class/thermal/");
            File[] files = dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    if (Pattern.matches("thermal_zone[0-9]+", file.getName())) {
                        return true;
                    }
                    return false;
                }
            });

            final int SIZE = files.length;
            String line = "";
            String type = "";
            for (int i = 0; i < SIZE; i++) {
                fr = new FileReader("/sys/class/thermal/thermal_zone" + i + "/type");
                br = new BufferedReader(fr);
                line = br.readLine();
                if (line != null) {
                    type = line;
                }

                fr = new FileReader("/sys/class/thermal/thermal_zone" + i + "/temp");
                br = new BufferedReader(fr);
                line = br.readLine();
                if (line != null) {
                    // MTK CPU
                    if (type.contains("cpu")) {
                        long temperature = Long.parseLong(line);
                        if (temperature < 0) {
                            temp = "Unknow";
                        } else {
                            temp = (float) (temperature / 1000.0) + "";
                        }
                    } else if (type.contains("tsens_tz_sensor")) {
                        // Qualcomm CPU
                        long temperature = Long.parseLong(line);
                        if (temperature < 0) {
                            temp = "Unknow";
                        } else if (temperature > 100){
                            temp = (float) (temperature / 10.0) + "";
                        } else {
                            temp = temperature + "";
                        }
                    }
                    else if(type.contains("soc")){
                        long temperature = Long.parseLong(line);
                        if (temperature < 0) {
                            temp = "Unknow";
                        } else {
                            temp = (float) (temperature / 1000.0) + "";
                        }
                    }

                }
            }

            if (fr != null) {
                fr.close();
            }
            if (br != null) {
                br.close();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (Exception e) {
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                }
            }
        }

        return temp;
    }
}

