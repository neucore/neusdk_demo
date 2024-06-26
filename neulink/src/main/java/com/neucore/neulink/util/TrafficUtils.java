package com.neucore.neulink.util;

import com.neucore.neulink.log.NeuLogUtils;
import com.neucore.neulink.NeulinkConst;

import java.io.RandomAccessFile;

public class TrafficUtils implements NeulinkConst{

    private static String TAG = TAG_PREFIX+"TrafficUtils";
    /** 传输信息文件的路径 */
    public static final String TRAFFIC_INFO_PATH = "/proc/uid_stat/";

    /** 上传信息文件的文件名 */
    public static final String TCP_SND = "/tcp_snd";

    /** 下载信息文件的文件名 */
    public static final String TCP_RCV = "/tcp_rcv";

    /**
     * 工具类，构造方法私有
     */
    private TrafficUtils() {

    }

    /**
     * get total network traffic, which is the sum of upload and download
     * traffic
     *
     * @return total traffic include received and send traffic
     */
    public static long getTrafficInfo(String uid) {
        final String rcvPath = TRAFFIC_INFO_PATH + uid + TCP_RCV;
        final String sndPath = TRAFFIC_INFO_PATH + uid + TCP_SND;
        long rcvTraffic = -1;
        long sndTraffic = -1;
        try {
            RandomAccessFile rafRcv = new RandomAccessFile(rcvPath, "r");
            rcvTraffic = Long.parseLong(rafRcv.readLine());
            rafRcv.close();

            RandomAccessFile rafSnd = new RandomAccessFile(sndPath, "r");
            sndTraffic = Long.parseLong(rafSnd.readLine());
            rafSnd.close();
        } catch (Exception e) {
            NeuLogUtils.eTag(TAG, e.getMessage(), e);
        }
        if (rcvTraffic == -1 || sndTraffic == -1) {
            return -1;
        } else {
            return (rcvTraffic + sndTraffic);
        }
    }
}
