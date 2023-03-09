package com.security.config;

import com.security.properties.SecretProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

@Configuration
public class JwtTokenConfig {

    @Autowired
    private SecretProperties secretProperties;

    @Bean(name = "jwtTokenStore")
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    //用于JWT令牌生成，需要设置用于签名解签名的secret密钥
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
        KeyStoreKeyFactory keyStoreKeyFactory =
                new KeyStoreKeyFactory(new ClassPathResource(secretProperties.getFile()), secretProperties.getPassword().toCharArray());
        System.out.println("keyStoreKeyFactory: " + keyStoreKeyFactory.getKeyPair(secretProperties.getAlias()));
        accessTokenConverter.setKeyPair(keyStoreKeyFactory.getKeyPair(secretProperties.getAlias()));
        return accessTokenConverter;
    }

    @Bean
    @ConditionalOnMissingBean(name = "jwtTokenEnhancer")
    public TokenEnhancer jwtTokenEnhancer() {
        return new JwtTokenEnhancer();
    }
}
