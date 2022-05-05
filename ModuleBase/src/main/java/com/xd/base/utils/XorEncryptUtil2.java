package com.xd.base.utils;


import java.nio.charset.StandardCharsets;

/**
 * data 2021/8/18.
 *
 * @author zero
 */
public class XorEncryptUtil2 {
    private static byte[] key = "JQYSd5HhnNFoR0-XDKTjqM8A9liOs7VPgpbavkfz2rcEwxUWGButCy614Im_L3eZ=".getBytes(StandardCharsets.UTF_8);
    private static byte[] flag = new byte[]{127,127,127};
    public static boolean isEncrypt(byte[] data) {
        if (data.length > flag.length) {
            if (data[0] == flag[0] && data[1] == flag[1] && data[2] == flag[2]) {
                return true;
            }
        }
        return false;
    }

    public static byte[] encrypt(byte[] data) {
        if (isEncrypt(data)) {
            return data;
        }
        byte[] result = new byte[data.length+flag.length];
        result[0]=flag[0];
        result[1]=flag[1];
        result[2]=flag[2];
        for (int i = 0; i < data.length; i++) {
            result[i+flag.length] = (byte) (data[i] ^ key[i % key.length]);
        }
        return result;
    }

    public static byte[] decrypt(byte[] data) {
        if (!isEncrypt(data)) {
            return data;
        }
        byte[] result = new byte[data.length-flag.length];
        for (int i = 0; i < data.length-flag.length; i++) {
            result[i] = (byte) (data[i+flag.length] ^ key[i % key.length]);
        }
        return result;
    }
}
