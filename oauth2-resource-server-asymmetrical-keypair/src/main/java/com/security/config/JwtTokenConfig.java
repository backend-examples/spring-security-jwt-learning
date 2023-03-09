package com.security.config;

import com.security.properties.SecretProperties;
import io.micrometer.core.instrument.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.io.IOException;

@Configuration
public class JwtTokenConfig {

    @Autowired
    private SecretProperties secretProperties;

    @Bean(name="jwtTokenStore")
    public TokenStore tokenStore(){
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    //用于JWT令牌生成，需要设置用于签名解签名的secret密钥
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
        Resource resource = new ClassPathResource(secretProperties.getFile());
        String publicKey = null;
        try {
            publicKey = IOUtils.toString(resource.getInputStream());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        accessTokenConverter.setVerifierKey(publicKey);
        return accessTokenConverter;
    }

    @Bean
    @ConditionalOnMissingBean(name = "jwtTokenEnhancer")
    public TokenEnhancer jwtTokenEnhancer(){
        return new JwtTokenEnhancer();
    }
}
