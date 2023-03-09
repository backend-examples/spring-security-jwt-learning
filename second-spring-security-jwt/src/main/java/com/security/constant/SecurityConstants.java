package com.security.constant;

public class SecurityConstants {

    public final static String TOKEN_HEADER = "Authorization";

    public static final String TOKEN_PREFIX = "Bearer ";

    public final static String REFRESH_TOKEN_HEADER = "refresh-token";

    /**
     * rememberMe 为 false 的时候过期时间是1个小时，这是1小时多少秒
     */
    public static final long EXPIRATION = 60 * 60L;

    /**
     * rememberMe 为 true 的时候过期时间是7天
     */
    public static final long EXPIRATION_REMEMBER = 60 * 60 * 24 * 7L;
}
