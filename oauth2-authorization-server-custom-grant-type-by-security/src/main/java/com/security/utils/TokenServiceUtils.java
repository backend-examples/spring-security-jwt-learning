package com.security.utils;

import ch.qos.logback.core.net.server.Client;
import com.security.config.JwtTokenEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
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

    public AuthorizationServerTokenServices getTokenService(ClientDetailsService clientDetailsService) {
        DefaultTokenServices services = new DefaultTokenServices();
        if (clientDetailsService != null) {
            services.setClientDetailsService(clientDetailsService);
        }
        services.setSupportRefreshToken(true);
        services.setTokenStore(jwtTokenStore);
        TokenEnhancerChain chain = new TokenEnhancerChain();
        chain.setTokenEnhancers(Arrays.asList(jwtAccessTokenConverter, jwtTokenEnhancer));
        services.setTokenEnhancer(chain);

        return services;
    }

    public AuthorizationServerTokenServices getTokenService() {
        DefaultTokenServices services = new DefaultTokenServices();
        services.setSupportRefreshToken(true);
        services.setTokenStore(jwtTokenStore);
        TokenEnhancerChain chain = new TokenEnhancerChain();
        chain.setTokenEnhancers(Arrays.asList(jwtAccessTokenConverter, jwtTokenEnhancer));
        services.setTokenEnhancer(chain);

        return services;
    }
}
