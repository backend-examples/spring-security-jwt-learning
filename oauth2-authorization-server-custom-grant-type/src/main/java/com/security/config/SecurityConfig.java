package com.security.config;

import com.security.granter.SmsAuthenticationProvider;
import com.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;
import java.util.Arrays;

@Configuration
public class SecurityConfig  extends WebSecurityConfigurerAdapter {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .formLogin()
                .loginPage("/base-login.html") //自定义的登录页面 **重要**
                .loginProcessingUrl("/login")
                // 用户通过security登录失败前往的页面
                .failureForwardUrl("/auth/loginFail")
                .and()
                .authorizeRequests()
                // /oauth/**接口必须在用户登录之后才能访问，否则用户未登录就先校验认证，不符合逻辑
                .antMatchers("/login", "/base-login.html", "/auth/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().disable();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 将Provider添加到认证管理器中
     *
     * @return
     * @throws Exception
     */
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        ProviderManager authenticationManager = new ProviderManager(Arrays.asList(new SmsAuthenticationProvider(userService, redisTemplate), daoAuthenticationProvider()));
        authenticationManager.setEraseCredentialsAfterAuthentication(false);
        return authenticationManager;
    }

    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userService);
        daoAuthenticationProvider.setHideUserNotFoundExceptions(false); // 设置显示找不到用户异常
        return daoAuthenticationProvider;
    }
}
