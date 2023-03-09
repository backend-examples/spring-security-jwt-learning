package com.security.config;

import com.security.exception.GlobalOAuth2WebResponseExceptionTranslator;
import com.security.interceptor.EndpointHandlerInterceptor;
import com.security.service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: @EnableAuthorizationServer注解开启OAuth2授权服务机制
 * @Package: com.security.config.OAuth2AuthorizationServer
 * @Version: 1.0
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServer extends AuthorizationServerConfigurerAdapter {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private DataSource dataSource;

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

    /**
     用来配置授权（authorization）以及令牌（token)的访问端点和令牌服务（token services）
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                // 认证服务器和资源服务器分开，两者都需要配置tokenStore
                .tokenStore(jwtTokenStore)
                .authenticationManager(authorizationManager)
                // 设置自定义的异常翻译器
                .exceptionTranslator(new GlobalOAuth2WebResponseExceptionTranslator())
                // 添加入口校验拦截器
                .addInterceptor(new EndpointHandlerInterceptor())
                // 刷新令牌的时候需要用户信息, 用户信息查询服务。不配置的话，刷新token是使用不了的
                .userDetailsService(userService)
                // 数据库管理授权信息
//                .approvalStore(new JdbcApprovalStore(dataSource))
                // 数据库管理授权码
//                .authorizationCodeServices(new JdbcAuthorizationCodeServices(dataSource))
                .pathMapping("/oauth/confirm_access", "/auth/confirm_access")
                .pathMapping("/oauth/token","/auth/token");

        // 整合JWT
        if (jwtAccessTokenConverter != null && jwtTokenEnhancer != null) {
            TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
            List<TokenEnhancer> enhancerList = new ArrayList<>();
            enhancerList.add(jwtTokenEnhancer);
            enhancerList.add(jwtAccessTokenConverter);
            tokenEnhancerChain.setTokenEnhancers(enhancerList);
            // jwt
            endpoints.tokenEnhancer(tokenEnhancerChain)
                    .accessTokenConverter(jwtAccessTokenConverter);
        }
    }

    //这个位置我们将Client客户端注册信息写死，后面章节我们会讲解动态实现
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //配置客户端存储到db 代替原来得内存模式
        JdbcClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
        clientDetailsService.setPasswordEncoder(passwordEncoder);
        clients.withClientDetails(clientDetailsService);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer
                .tokenKeyAccess("permitAll()")
                // 将checkTokenAccess的权限设置为isAuthenticated，认证通过才可以访问。
                .checkTokenAccess("isAuthenticated()")
                // 允许客户端访问OAuth2授权接口，否则返回401
                .allowFormAuthenticationForClients();
    }

}
