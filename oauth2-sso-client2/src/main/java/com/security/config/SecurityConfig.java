package com.security.config;

import com.security.handler.CustomLoginUrlAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.web.cors.CorsUtils;

/**
 * 如果使用的是spring-security-oauth2，必须单独引入spring-security-oauth2-autoconfigure
 * @EnableOAuth2Sso在spring-security-oauth2-autoconfigure依赖中
 */
@Configuration
//@EnableOAuth2Sso
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // 自定义重定向拦截过滤器
    @Autowired
    private OAuth2ClientAuthenticationProcessingFilter oauth2ClientAuthenticationProcessingFilter;

    @Autowired
    private SpringSessionBackedSessionRegistry<? extends Session> sessionRegistry;

    @Bean
    public CustomLoginUrlAuthenticationEntryPoint customLoginUrlAuthenticationEntryPoint() {
        return new CustomLoginUrlAuthenticationEntryPoint("/login");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                // 跨域配置
                .cors()
                .and()
                .csrf().disable()
                .authorizeRequests()
                // 放行option
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                // 放行登录失败接口
                .antMatchers("/toFail").permitAll()
                .anyRequest().authenticated()
                .and()
                // 配置自定义过滤器-注意在BasicAuthenticationFilter拦截之前处理
                .addFilterBefore(oauth2ClientAuthenticationProcessingFilter, BasicAuthenticationFilter.class);

        http
                .exceptionHandling()
                .authenticationEntryPoint(customLoginUrlAuthenticationEntryPoint());

        // 单点登录客户端不需要配置session并发管理
        // session并发管理
//        http
//                .sessionManagement()
//                .maximumSessions(1)
//                .maxSessionsPreventsLogin(true)
//                // 将内存管理修改为redis管理
//                .sessionRegistry(sessionRegistry);
    }

    /**
     * @功能描述: 静态资源忽略放行配置
     * @编写作者： lixx2048@163.com
     * @开发日期： 2020年7月26日
     * @历史版本： V1.0
     * @参数说明：
     * @返  回  值：
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        // 放行静态资源，否则添加oauth2情况下无法显示
        web.ignoring().antMatchers("/favor.ico", "/favicon.ico","/v2/api-docs", "/swagger-resources/configuration/ui",
                "/swagger-resources","/swagger-resources/configuration/security",
                "/swagger-ui.html","/css/**", "/js/**","/images/**", "/webjars/**", "**/favicon.ico", "/index");
    }
}
