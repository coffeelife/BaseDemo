package com.example.basemodule.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

    private static byte[] getMD5(String str) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(str.getBytes());
        byte[] m = md5.digest();//加密
        return m;
    }

    public static String get16MD5(String str) throws NoSuchAlgorithmException {
        return get16String(getMD5(str));
    }

    public static String get32MD5(String str) throws NoSuchAlgorithmException {
        return get32String(getMD5(str));
    }

    private static String get32String(byte[] b) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            int a = b[i];
            if (a < 0)
                a += 256;
            if (a < 16)
                buf.append("0");
            buf.append(Integer.toHexString(a));

        }
        return buf.toString();  //32位
    }

    private static String get16String(byte[] b) {
        return get32String(b).substring(8, 24);
    }
}
