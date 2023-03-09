package com.security.config;

import com.security.exception.GlobalOAuth2WebResponseExceptionTranslator;
import com.security.interceptor.EndpointHandlerInterceptor;
import com.security.service.UserService;
import com.security.utils.RedisUtil;
import com.security.utils.TokenServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
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

    @Autowired
    private TokenServiceUtils tokenServiceUtils;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//        //配置客户端存储到db 代替原来得内存模式
        JdbcClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
        clientDetailsService.setPasswordEncoder(passwordEncoder);
        clients.withClientDetails(clientDetailsService);
    }

    /**
     用来配置授权（authorization）以及令牌（token)的访问端点和令牌服务（token services）
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

        endpoints
                // 认证服务器和资源服务器分开，两者都需要配置tokenStore
                .tokenServices(tokenServiceUtils.getTokenService())
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
//                .pathMapping("/oauth/token","/auth/token")
                /**
                 * refresh_token有两种使用方式：重复使用(true)、非重复使用(false)，默认为true
                 * 1、重复使用：access_token过期刷新时，refresh_token过期时间未改变，仍以初次生成的时间为准
                 * 2、非重复使用：access_token过期刷新时，refresh_token过期时间延续，在refresh_token有效期内刷新便永不失效，达到无需再次登录的目的
                 */
                .reuseRefreshTokens(true);
    }

    // 令牌安全约束
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.tokenKeyAccess("permitAll()")
                // 将checkTokenAccess的权限设置为isAuthenticated，认证通过才可以访问。
                .checkTokenAccess("isAuthenticated()");
                // 开启表单验证
//                .allowFormAuthenticationForClients();
    }

}
