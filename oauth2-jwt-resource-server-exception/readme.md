## 资源服务器scope权限控制

资源服务器通过hasAnyScope配置了scope权限控制，这样认证服务器拿到的token如果scope不对，也无法获取对应的资源。
配置方法和security的HttpSecurity的方式一致

## 资源服务器重启

资源服务器重启，token校验过期。需要生成新的token
```java
// 获取token
private final TokenExtractor TOKEN_EXTRACTOR = new BearerTokenExtractor();

private JwtAccessTokenConverter jwtAccessTokenConverter;

@Autowired
public void setJwtAccessTokenConverter(JwtAccessTokenConverter jwtAccessTokenConverter) {
    this.jwtAccessTokenConverter = jwtAccessTokenConverter;
}

TokenStore tokenStore = new JwtTokenStore(jwtAccessTokenConverter);
```
去除以上代码，就不会了。但去掉就存在token格式错误和过期的异常统一处理的情况。

## HttpSecurity的配置
换了一种新方式配置，如下：
```java
http.antMatcher("/user/**").authorizeRequests()
                .antMatchers("/user/getUserInfo").access("#oauth2.hasAnyScope('user_base', 'user_userInfo')");

        // oauth2 授权后即可访问
        http.antMatcher("/**")
                .authorizeRequests()
                .antMatchers("/api/**")
                .access("#oauth2.isOAuth()");
```