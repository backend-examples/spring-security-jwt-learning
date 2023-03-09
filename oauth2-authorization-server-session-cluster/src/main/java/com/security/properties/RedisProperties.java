package com.security.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@PropertySource("classpath:application.yml")
public class RedisProperties {

    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private int port;

    @Value("${redis.password}")
    private String password;

    @Value("${redis.database}")
    private int database;

    @Value("${redis.lettuce.min-idle}")
    private int minIdle;

    @Value("${redis.lettuce.max-idle}")
    private int maxIdle;

    @Value("${redis.lettuce.max-active}")
    private int maxActive;

    @Value("${redis.lettuce.max-wait}")
    private int maxWait;

    @Value("${redis.lettuce.timeout}")
    private int timeout;

    @Value("${redis.lettuce.shutdown-timeout}")
    private int shutdownTimeout;
}