package com.neucore.neulink.util;

import android.util.Base64;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {
    public static final String KEY_ALGORITHMS_CBC = "AES/CBC/PKCS5Padding";
    public static final int KEY_SIZE = 256;
    public static final Charset ENCODE = StandardCharsets.UTF_8;
    private static final String INIT_VECTOR_V2 = "_6-%{>+,o_jaa,$G";
    private static final byte[] initVector = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static final String MSG_PATTERN = "加密异常:{}";

    public static SecretKey loadKeyAes(String base64Key) {
        byte[] bytes = Base64.decode(base64Key,Base64.DEFAULT);
        return new SecretKeySpec(bytes, "AES");
    }

    public static String v1V2Encrypt(String base64Key, String planText) {
        SecretKey key = loadKeyAes(base64Key);
        try {
            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR_V2.getBytes(StandardCharsets.UTF_8));
            final Cipher cipher = Cipher.getInstance(KEY_ALGORITHMS_CBC);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            byte[] encryptBytes = planText.getBytes(ENCODE);
            byte[] result = cipher.doFinal(encryptBytes);
            return Base64.encodeToString(result,Base64.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException("加密失败");
        }
    }

    /**
     * 为了兼容v2授权激活AES 解密字符串，base64Key对象
     *
     * @param base64Key
     * @param encryptData
     * @return
     */
    public static String v1V2Decrypt(String base64Key, String encryptData) {
        SecretKey key = loadKeyAes(base64Key);
        try {
            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR_V2.getBytes(StandardCharsets.UTF_8));
            final Cipher cipher = Cipher.getInstance(KEY_ALGORITHMS_CBC);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] decryptBytes = Base64.decode(encryptData,Base64.DEFAULT);
            byte[] result = cipher.doFinal(decryptBytes);
            return new String(result, ENCODE);
        } catch (Exception e) {
            throw new RuntimeException("解密失败");
        }
    }
}
