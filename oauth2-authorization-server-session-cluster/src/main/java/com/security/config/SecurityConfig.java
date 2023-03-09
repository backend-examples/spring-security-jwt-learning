package com.security.config;

import com.security.filter.ValidateCodeFilter;
import com.security.granter.SmsAuthenticationSecurityConfig;
import com.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.web.cors.CorsUtils;

import javax.annotation.Resource;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private UserService userService;

    @Autowired
    private SpringSessionBackedSessionRegistry<? extends Session> sessionRegistry;

    @Resource
    private ValidateCodeFilter validateCodeFilter;

    @Resource
    private SmsAuthenticationSecurityConfig smsAuthenticationSecurityConfig;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                // 开启跨域
                .cors()
                .and()
                .csrf().disable()
                .apply(smsAuthenticationSecurityConfig)
                .and()
                .formLogin()
                .loginPage("/base-login.html") //自定义的登录页面 **重要**
                .loginProcessingUrl("/login")
                // 用户通过security登录失败前往的页面
                .failureForwardUrl("/auth/loginFail")
                .and()
                .authorizeRequests()
                // 放行预请求
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                // /oauth/**接口必须在用户登录之后才能访问，否则用户未登录就先校验认证，不符合逻辑
                .antMatchers("/login", "/base-login.html", "/auth/**", "/oauth/sms", "/code/**").permitAll()
                .anyRequest().authenticated();

        // session并发管理
        http
                .sessionManagement()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(true)
                // 将内存管理修改为redis管理
                .sessionRegistry(sessionRegistry);

        // 添加验证码过滤器
        http
                .addFilterBefore(validateCodeFilter, AbstractPreAuthenticatedProcessingFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
