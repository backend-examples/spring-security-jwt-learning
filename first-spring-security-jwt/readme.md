# 第一版 spring-com.com.security + jwt
## 实现：
作为用来熟悉spring-security的开发的版本，一共混杂了两种实现方式。因为我一直想抛弃spring-security框架提供的默认的登录接口，实现通过controller
进行自定义登录逻辑开发。之前看了一些教程，误以为必须要配置http.formLogin().loginPage().loginProcessUrl()。
但其实这种实现方式的适用场景是前后端不分离的情况。前后端分离的场景需要禁用formLogin()。
### 前后端分离的实现
前后端分离的场景需要禁用formLogin()
>方式一：http.formLogin().disable()
> 
>方式二：不配置http.formLogin()

### 该版本存在的内容
1. 通过LoginAuthenticationFailureHandler配置的登录校验，使用JwtAuthenticationLoginFilter过滤器处理了自定义登录逻辑和登录接口(/login, method为post)，以及登录成功(LoginAuthenticationSuccessHandler)或失败(LoginAuthenticationFailureHandler)之后的处理器。
2. 通过controller实现的 `/auth/login` 登录接口。当通过该接口登录时，无论成功还是失败，都不会经过`loginAuthenticationFailureHandler`配置的处理器去处理结果。这两套流程是独立的，所以要想使用controller的接口去通过spring-security框架处理用户的认证结果，需要在自定义登录逻辑里调用
   `authentication = authenticationManager.authenticate(authenticationToken);`，该方法会去调用UserDetailsServiceImpl.loadUserByUsername，通过捕获它的异常，去抛出对应的spring-security的用户登录异常。
    完整代码如下：
   ```!
    // 用户验证
    Authentication authentication = null;
    try {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        AuthenticationContextHolder.setContext(authenticationToken);
        // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
        authentication = authenticationManager.authenticate(authenticationToken);
    } catch (Exception e) {
        if (e instanceof BadCredentialsException) {
            throw new UserPasswordNotMatchException();
        } else {
            throw new ServiceException(e.getMessage());
        }
    }  finally {
        AuthenticationContextHolder.clearContext();
    }
   ```
   以上代码只是处理用户的用户名或密码不匹配的错误，token验证还是`TokenAuthenticationFilter`进行验证的。

> 备注：之前我有一个误区，认controller实现的登录逻辑只需要保证用户输入的用户名密码正确后，就是登录成功，不需要进行token校验，访问需要特定角色的接口应该和使用formLogin()默认的登录接口一样，能根据当前登录角色实现权限鉴定，
> 结果一直提示用户未登录的异常。其实是我误以为登陆后自动走了spring-security的登录校验。仔细想想就是不通的，使用默认的登录接口，框架内部肯定进行了登陆了校验（具体是怎么校验的，我还不清楚）。而我未使用默认接口，也没主动去通过spring-security的相关api进行校验，肯定要提示未登录。
> 其实spring-security的登录逻辑就是对日常登录逻辑进行了封装，根本原理和不使用框架的登录逻辑是一样的。建议不懂的，先去搞明白未使用spring-security框架的token登录校验逻辑。

# SpringSecurity 用户名和密码的校验过程及自定义密码验证
1. 先将用于登录的账号密码存到UsernamePasswordAuthenticationToken

2. 执行 AuthenticationManager 认证方法authenticate(UsernamePasswordAuthenticationToken)

3. ProviderManager实现了AuthenticationManager.authenticate(UsernamePasswordAuthenticationToken)

4. ProviderManager 是通过自身管理的n个AuthenticationProvider认证提供者去进行认证

5. AuthenticationProvider认证提供者 使用自身的authenticate(Authentication)方法;

6. AuthenticationProvider的authenticate(Authentication)方法是被AbstractUserDetailsAuthenticationProvider所实现

7. AbstractUserDetailsAuthenticationProvider 抽象用户细节认证提供者 会调用 自身声明的retrieveUser抽象方法来检索用户

8. retrieveUser抽象方法在DaoAuthenticationProvider 持久层认证提供者 中进行了体现

9. DaoAuthenticationProvider 持久层认证提供者 包含 UserDetailsService 用户细节处理器,

10. 用户细节处理器的loadUserByUsername方法又被自定义的UserDetailsServiceImpl所实现

11. UserDetailsServiceImpl实现类取出数据库中的该登录名的数据(selectUserByUserName),并将用户的菜单权限数据和基本信息封装成一个UserDetails用户细节返回!

12. AbstractUserDetailsAuthenticationProvider 抽象用户细节认证提供者 最终获取到UserDeatils

13. 然后AbstractUserDetailsAuthenticationProvider 调用additionalAuthenticationChecks方法对用户的密码进行最后的检查

14. 密码的校验是由BCryptPasswordEncoder 通过实现PasswordEncoder 的matches方法来完成,

15. BCryptPasswordEncoder.matches 方法会校验密文是否属于自己的编码格式,最终密码校验的细节完全在BCrypt实体类中进行