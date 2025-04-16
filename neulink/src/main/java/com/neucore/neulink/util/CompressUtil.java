package com.neucore.neulink.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressUtil {

    /**
     *
     * @param primContent
     * @param encoding
     * @return
     */
    public static byte[] gzipCompress(String primContent, String encoding) {
        if (primContent == null || primContent.length() == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(primContent.getBytes(encoding));
        } catch (IOException e) {
        } finally {
            if (gzip != null) {
                try {
                    gzip.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return out.toByteArray();
    }

    /**
     *
     * @param primContent
     * @return
     */
    public static byte[] gzipCompress(byte[] primContent) {
        if (primContent == null || primContent.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(primContent);
        } catch (IOException e) {
        } finally {
            if (gzip != null) {
                try {
                    gzip.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return out.toByteArray();
    }

    /**
     *
     * @param compressed
     * @return
     */
    public static byte[] gzipUncompress(byte[] compressed) {
        if (compressed == null) {
            return null;
        }
        try {
            if (!isCompressed(compressed)) {
                return compressed;
            }
        } catch (IOException e) {
            return compressed;
        }
        ByteArrayInputStream in = null;
        GZIPInputStream ginzip = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            in = new ByteArrayInputStream(compressed);
            ginzip = new GZIPInputStream(in);

            byte[] buffer = new byte[1024];
            int offset = -1;
            while ((offset = ginzip.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
            return out.toByteArray();
        } catch (IOException e) {
        } finally {
            if (ginzip != null) {
                try {
                    ginzip.close();
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            try {
                out.close();
            } catch (IOException e) {
            }
        }
        return null;
    }

    public static boolean isCompressed(byte[] bytes) throws IOException {
        if ((bytes == null) || (bytes.length < 2)) {
            return false;
        } else {
            return ((bytes[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (bytes[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8)));
        }
    }
}
