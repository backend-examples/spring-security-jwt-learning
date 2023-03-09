package com.security.utils;

import com.security.config.JwtTokenEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;

@Component
public class TokenServiceUtils {

    @Resource
    private JwtTokenStore jwtTokenStore;

    @Resource
    private JwtTokenEnhancer jwtTokenEnhancer;

    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    // 这里的 token 的过期时间是官方默认的
    public DefaultTokenServices getTokenService() {
        DefaultTokenServices services = new DefaultTokenServices();
//        services.setSupportRefreshToken(true);
        services.setTokenStore(jwtTokenStore);
        TokenEnhancerChain chain = new TokenEnhancerChain();
        chain.setTokenEnhancers(Arrays.asList(jwtAccessTokenConverter, jwtTokenEnhancer));
        services.setTokenEnhancer(chain);

        return services;
    }
}
