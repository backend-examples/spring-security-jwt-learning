## 解决 vue 出现 Refused to display 'http://localhost:8001/' in a frame because it set 'X-Frame-Options' to 'deny'.

#### 缘由
因为项目使用了spring-security安全框架，Spring Security默认设置X-Frame-Options 为 deny：拒绝

#### 解决方案
因为Spring Security默认设置X-Frame-Options响应头是 DENY，
也就是不能被嵌入到任何iframe中，这也造成了我们无法正常显示eureka页面，所以我们需要在Spring Security的配置文件中设置关闭X-Frame-Options即可，也就是加入下面这句：
```markdown
.headers().frameOptions().disable();
```
完整代码如下：
```java
package com.security.config;

import com.security.handler.CustomLoginUrlAuthenticationEntryPoint;
import com.security.handler.CustomSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.web.cors.CorsUtils;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 如果使用的是spring-security-oauth2，必须单独引入spring-security-oauth2-autoconfigure
 * @EnableOAuth2Sso在spring-security-oauth2-autoconfigure依赖中
 */
@Configuration
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
                .csrf().disable().headers().frameOptions().disable()
                .and()
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
```
重启之后就可以正常访问了。

## X-Frame-Options
下面介绍下X-Frame-Options主要用处是用于防止点击劫持，点击劫持（ClickJacking）是一种视觉上的欺骗手段。攻击者使用一个透明的iframe，覆盖在一个网页上，然后诱使用户在网页上进行操作，此时用户将在不知情的情况下点击透明的iframe页面。通过调整iframe页面的位置，可以诱使用户恰好点击在iframe页面的一些功能性按钮上。 HTTP响应头信息中的X-Frame-Options，可以指示浏览器是否应该加载一个iframe中的页面。如果服务器响应头信息中没有X-Frame-Options，则该网站存在ClickJacking攻击风险。网站可以通过设置X-Frame-Options阻止站点内的页面被其他页面嵌入从而防止点击劫持。

X-Frame-Options响应头。赋值有如下三种：
* DENY：不能被嵌入到任何iframe或者frame中。
* SAMEORIGIN：页面只能被本站页面嵌入到iframe或者frame中
* ALLOW-FROM uri：只能被嵌入到指定域名的框架中
