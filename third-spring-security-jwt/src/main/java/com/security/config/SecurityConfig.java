package com.security.config;

import com.security.filter.TokenAuthenticationFilter;
import com.security.handler.EntryPointUnauthorizedHandler;
import com.security.handler.RequestAccessDeniedHandler;
import com.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * prePostEnabled = true 的作用的是启用Spring Security的@PreAuthorize 以及@PostAuthorize 注解。
 * securedEnabled = true 的作用是启用Spring Security的@Secured 注解。
 * jsr250Enabled = true 的作用是启用@RoleAllowed 注解
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true) // 开启权限注解开发
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Autowired
    private EntryPointUnauthorizedHandler entryPointUnauthorizedHandler;

    @Autowired
    private RequestAccessDeniedHandler requestAccessDeniedHandler;

    @Autowired
    private TokenAuthenticationFilter tokenAuthenticationFilter;

    /**
     * 解决 无法直接注入 AuthenticationManager
     *
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception
    {
        return super.authenticationManagerBean();
    }

    /**
     * 加密
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder () {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                // 基于token，所以不需要session。必须关掉，否则会导致只要使用token登录成功一次，之后不携带token，也能访问需要登录的接口
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);;

        http
                .authorizeRequests()
                // /auth/register、/auth/login允许匿名访问
                .antMatchers("/auth/register", "/auth/login").anonymous()
                .anyRequest().authenticated();

        http
                .exceptionHandling()
                .authenticationEntryPoint(entryPointUnauthorizedHandler)
                .accessDeniedHandler(requestAccessDeniedHandler);

        // 将TOKEN校验过滤器配置到过滤器链中，否则不生效，放到UsernamePasswordAuthenticationFilter之前
        http
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

//        http.addFilterAfter()
    }
}
