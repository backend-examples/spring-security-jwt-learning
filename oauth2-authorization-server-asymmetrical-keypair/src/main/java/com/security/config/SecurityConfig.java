package com.security.config;

import com.security.filter.ValidateCodeFilter;
import com.security.granter.SmsAuthenticationSecurityConfig;
import com.security.handler.CustomAuthenticationFailureHandler;
import com.security.handler.CustomSavedRequestAwareAuthenticationSuccessHandler;
import com.security.handler.SessionInformationExpiredStrategyHandler;
import com.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
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

    @Resource
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Resource
    private CustomSavedRequestAwareAuthenticationSuccessHandler customSavedRequestAwareAuthenticationSuccessHandler;

    @Resource
    private SessionInformationExpiredStrategyHandler sessionInformationExpiredStrategyHandler;

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
                .headers().frameOptions().disable()
                .and()
                .apply(smsAuthenticationSecurityConfig)
                .and()
                .formLogin()
                .loginPage("/auth/base-login") //自定义的登录页面 **重要**
                .loginProcessingUrl("/auth/login")
                // 用户通过security登录失败的处理
                .failureHandler(customAuthenticationFailureHandler)
                .successHandler(customSavedRequestAwareAuthenticationSuccessHandler)
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
                // 当达到一个用户的允许的最大会话数量时，是否允许进行登录。为true，不允许；为false，会挤掉之前的登录进行登录
                // 我这里改为允许挤掉直接登录
                .maxSessionsPreventsLogin(false)
                .expiredSessionStrategy(sessionInformationExpiredStrategyHandler)
                // 将内存管理修改为redis管理
                .sessionRegistry(sessionRegistry)
                .and()
                /* 配置会话固定攻击的策略为migrateSession。这样会创建一个新的session，并将旧session里的内容复制进来。这样做会触发session销毁的监听器。 */
                .sessionFixation().migrateSession();;

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
