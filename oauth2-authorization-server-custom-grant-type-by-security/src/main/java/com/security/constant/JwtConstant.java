package com.security.constant;

public class JwtConstant {
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * @Feild ACCESS_TOKEN_EXPIRE_TIME: accessToken过期时间为1小时
     */
    public static final Integer ACCESS_TOKEN_EXPIRE_TIME = 60 * 60;

    /**
     * @Feild REFRESH_TOKEN_EXPIRE_TIME: refreshToken过期时间为6小时
     */
    public static final Integer REFRESH_TOKEN_EXPIRE_TIME = 60 * 60 * 6;
}
