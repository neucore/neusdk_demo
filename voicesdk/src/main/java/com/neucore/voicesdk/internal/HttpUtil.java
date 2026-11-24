package com.neucore.voicesdk.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 极简 HTTP 工具，避免引入三方网络库。
 */
public final class HttpUtil {

    private HttpUtil() {
    }

    public static byte[] post(String url, byte[] body, String contentType, int connectTimeoutMs, int readTimeoutMs)
            throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(connectTimeoutMs);
            conn.setReadTimeout(readTimeoutMs);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", contentType);
            conn.setRequestProperty("Accept", "*/*");
            conn.setFixedLengthStreamingMode(body.length);

            OutputStream os = conn.getOutputStream();
            os.write(body);
            os.flush();
            os.close();

            int code = conn.getResponseCode();
            if (code < 200 || code >= 300) {
                throw new IOException("HTTP " + code + " from " + url + ": " + readError(conn));
            }
            return readAll(conn.getInputStream());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static String get(String url, int connectTimeoutMs, int readTimeoutMs) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(connectTimeoutMs);
            conn.setReadTimeout(readTimeoutMs);
            int code = conn.getResponseCode();
            if (code < 200 || code >= 300) {
                throw new IOException("HTTP " + code + " from " + url);
            }
            return new String(readAll(conn.getInputStream()), "UTF-8");
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static String readError(HttpURLConnection conn) {
        try {
            InputStream in = conn.getErrorStream();
            if (in == null) {
                return "";
            }
            return new String(readAll(in), "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }

    private static byte[] readAll(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int n;
        while ((n = in.read(buf)) > 0) {
            out.write(buf, 0, n);
        }
        in.close();
        return out.toByteArray();
    }
}
