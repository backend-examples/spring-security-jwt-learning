package com.security.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.security.listener.SessionListener;
import com.security.properties.RedisProperties;
import com.security.serializer.OAuth2AuthenticationDetailsMixin;
import com.security.serializer.OAuth2AuthenticationMixin;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.RequestContextFilter;

import java.time.Duration;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.Map;

@Configuration
public class RedisConfig {

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public LettuceConnectionFactory connectionFactory() {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(redisProperties.getMaxIdle());
        genericObjectPoolConfig.setMinIdle(redisProperties.getMinIdle());
        genericObjectPoolConfig.setMaxTotal(redisProperties.getMaxActive());
        genericObjectPoolConfig.setMaxWaitMillis(redisProperties.getMaxWait());
        genericObjectPoolConfig.setTimeBetweenEvictionRunsMillis(redisProperties.getShutdownTimeout());
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());
        redisStandaloneConfiguration.setHostName(redisProperties.getHost());
        redisStandaloneConfiguration.setPort(redisProperties.getPort());
        redisStandaloneConfiguration.setPassword(RedisPassword.of(redisProperties.getPassword()));
        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(redisProperties.getTimeout()))
                .shutdownTimeout(Duration.ofMillis(redisProperties.getShutdownTimeout()))
                .poolConfig(genericObjectPoolConfig)
                .build();

        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfig);
//        factory.setShareNativeConnection(true);
//        factory.setValidateConnection(false);
        return factory;
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();

        BasicPolymorphicTypeValidator basicPolymorphicTypeValidator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(Collection.class)
                .allowIfSubType(Number.class)
                .allowIfSubType(Map.class)
                .allowIfSubType(Temporal.class)
                .allowIfSubType(Object.class)
                .allowIfSubType(Byte.class)
                .allowIfSubTypeIsArray()
                .build();
        objectMapper.activateDefaultTyping(basicPolymorphicTypeValidator, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        // 解决SecurityUser中没有Set方法导致的序列化失败的错误
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

//        objectMapper.addMixIn(OAuth2AuthenticationDetails.class, OAuth2AuthenticationDetailsMixin.class);
        // OAuth2Authentication必须手动实现反序列化
        objectMapper.addMixIn(OAuth2Authentication.class, OAuth2AuthenticationMixin.class);

        /**
         * 解决如下问题：
         * org.springframework.data.redis.serializer.SerializationException:
         * Could not read JSON: Cannot construct instance of `org.springframework.security.web.savedrequest.DefaultSavedRequest`
         * (no Creators, like default constructor, exist): cannot deserialize from Object value (no delegate- or property-based Creator)
         */
        objectMapper.registerModules(SecurityJackson2Modules.getModules(getClass().getClassLoader()));

        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // 设置键（key）的序列化采用StringRedisSerializer。
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    @Bean
    public RequestContextListener requestContextFilter() {
        return new RequestContextListener();
    }

    @Bean
    public RedisSerializer<?> springSessionDefaultRedisSerializer(RedisTemplate<?, ?> redisTemplate) {
        return redisTemplate.getValueSerializer();
    }
}
