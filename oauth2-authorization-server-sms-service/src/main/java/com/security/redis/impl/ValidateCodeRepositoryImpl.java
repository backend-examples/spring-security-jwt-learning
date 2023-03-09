package com.security.redis.impl;

import com.security.exception.ValidateCodeException;
import com.security.redis.ValidateCodeRepository;
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

    private final @NonNull RedisTemplate<String, String> redisTemplate;

    @Override
    public void save(String phoneOrEmail, String code, String type) {
        redisTemplate.opsForValue().set(buildKey(phoneOrEmail, type), code,
                //  有效期可以从配置文件中读取或者请求中读取
                Duration.ofMinutes(10).getSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public String get(String phoneOrEmail, String type) {
        return redisTemplate.opsForValue().get(buildKey(phoneOrEmail, type));
    }

    @Override
    public void remove(String phoneOrEmail, String type) {
        redisTemplate.delete(buildKey(phoneOrEmail, type));
    }

    private String buildKey(String phoneOrEmail, String type) {
        if (StringUtils.isEmpty(phoneOrEmail)) {
            throw new ValidateCodeException("请求中不存在账号");
        }

        return "code:" + type + ":" +  phoneOrEmail;
    }
}
