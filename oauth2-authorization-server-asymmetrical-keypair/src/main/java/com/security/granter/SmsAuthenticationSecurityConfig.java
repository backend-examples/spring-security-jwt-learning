package com.security.granter;

import com.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

/**
 * sms 配置
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/7/29 下午11:33
 */
@Component
public class SmsAuthenticationSecurityConfig
        extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Autowired
    @SuppressWarnings("all")
    private UserService userService;
    @Autowired
    @SuppressWarnings("all")
    private SmsSuccessHandler smsSuccessHandler;

    @Autowired
    @SuppressWarnings("all")
    private SmsFailureHandler smsFailureHandler;

    @Override
    public void configure(HttpSecurity http) {
        // 过滤器
        SmsAuthenticationFilter smsAuthenticationFilter = new SmsAuthenticationFilter();
        smsAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        smsAuthenticationFilter.setAuthenticationSuccessHandler(smsSuccessHandler);
        smsAuthenticationFilter.setAuthenticationFailureHandler(smsFailureHandler);

        // 授权提供者
        SmsAuthenticationProvider smsAuthenticationProvider = new SmsAuthenticationProvider();
        smsAuthenticationProvider.setUserService(userService);

        // 过滤器
        http.authenticationProvider(smsAuthenticationProvider)
                .addFilterAfter(smsAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
