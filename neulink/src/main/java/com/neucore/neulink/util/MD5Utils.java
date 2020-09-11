package com.neucore.neulink.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class MD5Utils {

    private static char[]                  digits  = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c',
            'd', 'e', 'f'                         };

    private static Map<Character, Integer> rDigits = new HashMap<Character, Integer>(16);
    static {
        for (int i = 0; i < digits.length; ++i) {
            rDigits.put(digits[i], i);
        }
    }

    private static MD5Utils                me      = new MD5Utils();
    private MessageDigest mHasher;
    private ReentrantLock opLock  = new ReentrantLock();

    private MD5Utils(){
        try {
            mHasher = MessageDigest.getInstance("md5");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static MD5Utils getInstance() {
        return me;
    }

    public String getMD5String(String content) {
        return bytes2string(hash(content));
    }

    public String getMD5String(byte[] content) {
        return bytes2string(hash(content));
    }

    public byte[] getMD5Bytes(byte[] content) {
        return hash(content);
    }

    /**
     * 对字符串进行md5
     *
     * @param str
     * @return md5 byte[16]
     */
    public byte[] hash(String str) {
        opLock.lock();
        try {
            byte[] bt = mHasher.digest(str.getBytes("UTF-8"));
            if (null == bt || bt.length != 16) {
                throw new IllegalArgumentException("md5 need");
            }
            return bt;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("unsupported utf-8 encoding", e);
        } finally {
            opLock.unlock();
        }
    }

    /**
     * 对二进制数据进行md5
     *
     * @param data
     * @return md5 byte[16]
     */
    public byte[] hash(byte[] data) {
        opLock.lock();
        try {
            byte[] bt = mHasher.digest(data);
            if (null == bt || bt.length != 16) {
                throw new IllegalArgumentException("md5 need");
            }
            return bt;
        } finally {
            opLock.unlock();
        }
    }

    /**
     * 将一个字节数组转化为可见的字符串
     *
     * @param bt
     * @return
     */
    public String bytes2string(byte[] bt) {
        int l = bt.length;

        char[] out = new char[l << 1];

        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = digits[(0xF0 & bt[i]) >>> 4];
            out[j++] = digits[0x0F & bt[i]];
        }

        return new String(out);
    }

    /**
     * 将字符串转换为bytes
     *
     * @param str
     * @return byte[]
     */
    public byte[] string2bytes(String str) {
        if (null == str) {
            throw new IllegalArgumentException("str is null");
        }
        if (str.length() != 32) {
            throw new IllegalArgumentException("str.length() != 32");
        }

        byte[] data = new byte[16];
        char[] chs = str.toCharArray();
        for (int i = 0; i < 16; ++i) {
            int h = rDigits.get(chs[i * 2]).intValue();
            int l = rDigits.get(chs[i * 2 + 1]).intValue();
            data[i] = (byte) ((h & 0x0F) << 4 | (l & 0x0F));
        }
        return data;
    }

    /**
     * 获取时间
     *
     * @return 时间戳 例如:201805
     */


    public String getMD5File(String path) throws IOException, NoSuchAlgorithmException {
        BigInteger bi = null;
        FileInputStream fis = null;
        try {
            byte[] buffer = new byte[8192];
            int len = 0;
            MessageDigest md = MessageDigest.getInstance("MD5");
            File f = new File(path);
            fis = new FileInputStream(f);
            while ((len = fis.read(buffer)) != -1) {
                md.update(buffer, 0, len);
            }
            fis.close();
            byte[] b = md.digest();
            bi = new BigInteger(1, b);
        }
        finally {
            if(fis!=null){
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
        }
        return bi.toString(16);
    }

}