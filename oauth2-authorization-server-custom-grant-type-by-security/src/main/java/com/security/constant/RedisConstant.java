package com.security.constant;

public class RedisConstant {
    // 为了保证 redis 的key不会因为 不同的业务可能会相同  所以一般会在这里加上前缀
    public static final String REDIS_UMS_PREFIX = "portal:authCode";
    // redis缓存过期时间 我们设置为 5分钟  一般验证码时间防止暴力破解 时间都很短
    public static final Long REDIS_UMS_EXPIRE = 60 * 5L;
    // 用于禁止多设备登录的token的key
    public static final String UNIQUE_USER_PREFIX = "unique-user:";
    // 用于黑名单的token的key
    public static final String TOKEN_JTI = "jti:";
}
