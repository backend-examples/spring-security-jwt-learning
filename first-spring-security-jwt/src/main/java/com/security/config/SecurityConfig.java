package com.security.config;

import com.security.filter.TokenAuthenticationFilter;
import com.security.handler.EntryPointUnauthorizedHandler;
import com.security.handler.RequestAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationSecurityConfig jwtAuthenticationSecurityConfig;

    @Autowired
    private EntryPointUnauthorizedHandler entryPointUnauthorizedHandler;
    @Autowired
    private RequestAccessDeniedHandler requestAccessDeniedHandler;

    // 自定义的Jwt Token校验过滤器
    @Bean
    public TokenAuthenticationFilter authenticationTokenFilterBean() {
        return new TokenAuthenticationFilter();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // CSRF禁用，因为不使用session
        http.csrf().disable();

        http.formLogin()
                //禁用表单登录，前后端分离用不上
                .disable()
                //应用登录过滤器的配置，配置分离
                .apply(jwtAuthenticationSecurityConfig);

        // 除了/auth/login、/auth/register可匿名访问，其他接口都需要登录后才能访问
        http
                .authorizeRequests()
                .antMatchers("/auth/login", "/auth/register").anonymous()
                .antMatchers("/admin/**").hasRole("admin")
                .antMatchers("/user/**").hasRole("user")
                .anyRequest().authenticated();

        http
                .exceptionHandling()
                //认证未通过，不允许访问异常处理器
                .authenticationEntryPoint(entryPointUnauthorizedHandler)
                //认证通过，但是没权限处理器
                .accessDeniedHandler(requestAccessDeniedHandler);

        http
                //将TOKEN校验过滤器配置到过滤器链中，否则不生效，放到UsernamePasswordAuthenticationFilter之前
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
    }
}
