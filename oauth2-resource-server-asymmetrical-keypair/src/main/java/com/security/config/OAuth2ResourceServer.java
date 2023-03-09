package com.security.config;

import com.security.handler.ExtendAccessDeniedHandler;
import com.security.handler.ExtendAuthenticationEntryPointHandler;
import com.security.utils.TokenServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import javax.annotation.Resource;

/**
 * 资源服务器
 * 优先级高于security
 * OAuth2AuthenticationProcessingFilter
 */
@Configuration
@EnableResourceServer
public class OAuth2ResourceServer extends ResourceServerConfigurerAdapter {

    @Autowired
    private ExtendAuthenticationEntryPointHandler extendAuthenticationEntryPointHandler;

    @Autowired
    private ExtendAccessDeniedHandler extendAccessDeniedHandler;

    @Autowired
    private TokenServiceUtils tokenServiceUtils;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.tokenServices(tokenServiceUtils.getTokenService());

        // token异常类重写
        resources.authenticationEntryPoint(extendAuthenticationEntryPointHandler);
        // 权限不足异常类重写
        resources.accessDeniedHandler(extendAccessDeniedHandler);
    }

    /**
     * 下面的配置顺序不能反
     * @param http
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {

//        // /api/** 接口登录后即可访问
//        http.authorizeRequests()
//                // /user/getUserInfo 接口的scope必须是 user_base、user_userInfo其中之一
//                .antMatchers("/user/getUserInfo").access("#oauth2.hasAnyScope('user_base', 'user_userInfo')")
//                // /api/** 接口的scope必须是all
//                .antMatchers("/api/**").access("#oauth2.hasAnyScope('all')")
//                .anyRequest().authenticated();

        http.antMatcher("/user/**").authorizeRequests()
                .antMatchers("/user/getUserInfo").access("#oauth2.hasAnyScope('user_base', 'user_userInfo', 'profile')");

        // oauth2 授权后即可访问
        http.antMatcher("/**")
                .authorizeRequests()
                .antMatchers("/api/**")
//                .access("#oauth2.hasAnyScope('all')");
                .access("#oauth2.isOAuth()");
    }

}
