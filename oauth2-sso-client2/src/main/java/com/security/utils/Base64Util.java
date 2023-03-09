package com.security.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Util {

    /**
     * base64编码
     * @param str
     * @return
     */
    public static String encode(String str) {
        return Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * base64解码
     * @param str
     * @return
     */
    public static String decode(String str) {
        return new String(Base64.getDecoder().decode(str), StandardCharsets.UTF_8);
    }
}
