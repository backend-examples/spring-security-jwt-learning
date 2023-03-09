package com.security.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

@Slf4j
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 60 * 60 * 24, redisNamespace = "spring:auth")
public class HttpSessionConfig {

    @Autowired
    private FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    @Bean
    public SpringSessionBackedSessionRegistry<? extends Session> sessionRegistry() {
        return new SpringSessionBackedSessionRegistry<>(sessionRepository);
    }
}
