package com.security.config;

import com.security.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServer extends AuthorizationServerConfigurerAdapter {

    @Resource
    private PasswordEncoder passwordEncoder;

    // 密码模式必须注入，否则会报错
    @Resource
    private AuthenticationManager authorizationManager;

    @Resource
    private UserService userService;

    @Resource
    private RedisConnectionFactory connectionFactory;

    @Resource
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Resource
    private TokenEnhancer jwtTokenEnhancer;

    @Resource
    private TokenStore jwtTokenStore;

    @Bean
    public TokenStore tokenStore() {
        RedisTokenStore redis = new RedisTokenStore(connectionFactory);
        return redis;
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                // 认证服务器和资源服务器分开，两者都需要配置tokenStore
                .tokenStore(jwtTokenStore)
                .authenticationManager(authorizationManager)
                // 刷新令牌的时候需要用户信息
                .userDetailsService(userService);

        //整合JWT
        if (jwtAccessTokenConverter != null && jwtTokenEnhancer != null) {
            TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
            List<TokenEnhancer> enhancerList = new ArrayList<>();
            enhancerList.add(jwtTokenEnhancer);
            enhancerList.add(jwtAccessTokenConverter);
            tokenEnhancerChain.setTokenEnhancers(enhancerList);
            //jwt
            endpoints.tokenEnhancer(tokenEnhancerChain)
                    .accessTokenConverter(jwtAccessTokenConverter);
        }
    }

    //这个位置我们将Client客户端注册信息写死，后面章节我们会讲解动态实现
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("client1").secret(passwordEncoder.encode("123456")) // Client 账号、密码。
                .redirectUris("http://localhost:8888/callback") // 配置回调地址，选填。
                .authorizedGrantTypes("authorization_code", "password", "implicit", "client_credentials", "refresh_token") // 授权码模式、密码模式、简化模式、客户端模式，支持刷新token
                .scopes("all") // 可授权的 Scope
                .accessTokenValiditySeconds(60*60) // accessToken的过期时间为1小时
                .refreshTokenValiditySeconds(60*60); // refreshToken的过期时间为1小时
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.tokenKeyAccess("permitAll()")
                // 将checkTokenAccess的权限设置为isAuthenticated，认证通过才可以访问。
                .checkTokenAccess("isAuthenticated()")
                .allowFormAuthenticationForClients();
    }

}
