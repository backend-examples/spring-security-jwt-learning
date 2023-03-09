package com.security.redis.impl;

import com.security.exception.ValidateCodeException;
import com.security.redis.ValidateCodeRepository;
import com.security.utils.RedisUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * redis 验证码操作
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/7/28 下午10:44
 */
@Component
@RequiredArgsConstructor
public class ValidateCodeRepositoryImpl implements ValidateCodeRepository {

    private final @NonNull RedisUtil redisUtil;

    @Override
    public void save(String phoneOrEmail, String code, String type) {
        //  有效期可以从配置文件中读取或者请求中读取
        redisUtil.set(buildKey(phoneOrEmail, type), code, Duration.ofMinutes(10).getSeconds());
    }

    @Override
    public String get(String phoneOrEmail, String type) {
        return (String) redisUtil.get(buildKey(phoneOrEmail, type));
    }

    @Override
    public void remove(String phoneOrEmail, String type) {
        redisUtil.del(buildKey(phoneOrEmail, type));
    }

    private String buildKey(String phoneOrEmail, String type) {
        if (StringUtils.isEmpty(phoneOrEmail)) {
            throw new ValidateCodeException("请求中不存在账号");
        }

        return "code:" + type + ":" + phoneOrEmail;
    }
}
