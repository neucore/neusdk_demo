package com.neucore.neulink.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressUtil {

    /**
     *
     * @param str
     * @param encoding
     * @return
     */
    public static byte[] gzipCompress(String str, String encoding) {
        if (str == null || str.length() == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes(encoding));
            return out.toByteArray();
        } catch (IOException e) {
        }
        finally {
            try {
                gzip.close();
            } catch (IOException e) {
            }
        }
        return null;
    }

    /**
     *
     * @param bytes
     * @return
     */
    public static byte[] gzipCompress(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        GZIPOutputStream gzip = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            gzip = new GZIPOutputStream(out);
            gzip.write(bytes);
            return out.toByteArray();
        } catch (IOException e) {
        }
        finally {
            try {
                gzip.close();
            } catch (IOException e) {
            }
        }
        return null;
    }
    /**
     *
     * @param bytes
     * @return
     */
    public static byte[] gzipUncompress(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        GZIPInputStream ungzip = null;
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            ungzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        } catch (IOException e) {
        }
        finally {
            try {
                out.close();
            } catch (IOException e) {
            }
            try {
                ungzip.close();
            } catch (IOException e) {
            }
        }
        return null;
    }
}
