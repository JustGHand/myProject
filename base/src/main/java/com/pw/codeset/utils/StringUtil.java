package com.pw.codeset.utils;

public class StringUtil {

    public static boolean isBlank(String string) {
        if (string == null||string.equals("")) {
            return true;
        }
        return false;
    }

}
