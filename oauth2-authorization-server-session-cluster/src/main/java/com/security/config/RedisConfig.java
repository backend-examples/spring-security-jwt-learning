package com.security.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.security.granter.SmsAuthenticationToken;
import com.security.properties.RedisProperties;
import com.security.serializer.SmsAuthenticationTokenMixin;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.context.request.RequestContextListener;

import java.time.Duration;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(method.getName());
            for (Object obj : params) {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }

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

        // 解决自定义短信验证码授权模式反序列化失败的问题
        objectMapper.addMixIn(SmsAuthenticationToken.class, SmsAuthenticationTokenMixin.class);

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

//    @Bean
//    public HttpSessionEventPublisher httpSessionEventPublisher() {
//        return new HttpSessionEventPublisher();
//    }

    /**
     * 必须在这里进行session进行redis的序列化配置
     *
     * @return
     */
    @Bean
    public RedisSerializer<?> springSessionDefaultRedisSerializer(RedisTemplate<?, ?> redisTemplate) {
//        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
//
//        ObjectMapper objectMapper = new ObjectMapper();
////        objectMapper.registerModule(new CoreJackson2Module());
////        objectMapper.registerModule(new WebJackson2Module());
////        objectMapper.registerModule(new WebServerJackson2Module());
////        objectMapper.registerModule(new WebServletJackson2Module());
////        objectMapper.registerModule(new OAuth2ClientJackson2Module());
//
//        // 构建一个基本的白名单
//        BasicPolymorphicTypeValidator basicPolymorphicTypeValidator = BasicPolymorphicTypeValidator.builder()
//                .allowIfSubType(Collection.class)
//                .allowIfSubType(Number.class)
//                .allowIfSubType(Map.class)
//                .allowIfSubType(Temporal.class)
//                .allowIfSubType(Object.class)
//                .allowIfSubTypeIsArray()
//                .build();
//        objectMapper.activateDefaultTyping(basicPolymorphicTypeValidator, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
//
//        // 解决SecurityUser中没有Set方法导致的序列化失败的错误
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//
//        /**
//         * 解决如下问题：
//         * org.springframework.data.redis.serializer.SerializationException:
//         * Could not read JSON: Cannot construct instance of `org.springframework.security.web.savedrequest.DefaultSavedRequest`
//         * (no Creators, like default constructor, exist): cannot deserialize from Object value (no delegate- or property-based Creator)
//         */
//        objectMapper.registerModules(SecurityJackson2Modules.getModules(getClass().getClassLoader()));
//
//        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
//
//        return jackson2JsonRedisSerializer;
        return redisTemplate.getValueSerializer();
    }

//    /**
//     * 监听session
//     */
//    @Bean
//    public SessionListener redisSessionListener() {
//        return new SessionListener();
//    }

    //    /**
//     * 设置spring session redis 序列化方式
//     * 这种序列化配置方式，返回值必须是RedisTemplate<Object, Object>
//     * @param factory
//     * @return
//     */
//    @Bean
//    public SessionRepository sessionRepository(RedisConnectionFactory redisConnectionFactory) {
//        RedisOperationsSessionRepository sessionRepository = new RedisOperationsSessionRepository(redisTemplate(redisConnectionFactory));
//        FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
//        sessionRepository.setDefaultSerializer(fastJsonRedisSerializer);
//        sessionRepository.setDefaultMaxInactiveInterval(36000);
//        return sessionRepository;
//    }
}