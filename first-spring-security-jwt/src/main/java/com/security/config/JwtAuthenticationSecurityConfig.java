package com.security.config;

import com.security.filter.JwtAuthenticationLoginFilter;
import com.security.handler.LoginAuthenticationFailureHandler;
import com.security.handler.LoginAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 登录过滤器的配置类
 */
@Configuration
public class JwtAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    /**
     * Qualifier指定 UserDetailsService 的实现类为 userDetailServiceImpl
     */
    @Qualifier("jwtTokenUserDetailsServiceImpl")
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * 登录成功处理器
     */
    @Autowired
    private LoginAuthenticationSuccessHandler loginAuthenticationSuccessHandler;

    /**
     * 登录失败处理器
     */
    @Autowired
    private LoginAuthenticationFailureHandler loginAuthenticationFailureHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 将登录接口的过滤器配置到过滤器链中
     * 1. 配置登录成功、失败处理器
     * 2. 配置自定义的userDetailService（从数据库中获取用户数据）
     * 3. 将自定义的过滤器配置到spring security的过滤器链中，配置在UsernamePasswordAuthenticationFilter之前
     * @param http
     */
    @Override
    public void configure(HttpSecurity http) {
        JwtAuthenticationLoginFilter filter = new JwtAuthenticationLoginFilter();
        filter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        // 认证成功处理器
        filter.setAuthenticationSuccessHandler(loginAuthenticationSuccessHandler);
        // 认证失败处理器
        filter.setAuthenticationFailureHandler(loginAuthenticationFailureHandler);
        // 直接使用DaoAuthenticationProvider
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        // 设置userDetailService
        provider.setUserDetailsService(userDetailsService);
        // 设置加密算法
        provider.setPasswordEncoder(passwordEncoder());
        http.authenticationProvider(provider);
        //将这个过滤器添加到UsernamePasswordAuthenticationFilter之前执行
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }
}
