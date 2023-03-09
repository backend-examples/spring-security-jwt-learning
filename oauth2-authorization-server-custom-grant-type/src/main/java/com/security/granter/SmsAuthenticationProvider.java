package com.security.granter;

import com.security.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;

public class SmsAuthenticationProvider implements AuthenticationProvider {

    private UserService userService;

    private RedisTemplate<String, String> redisTemplate;

    public SmsAuthenticationProvider(UserService userService, RedisTemplate<String, String> redisTemplate) {
        this.userService = userService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SmsAuthenticationToken authenticationToken = (SmsAuthenticationToken) authentication;
        Object principal = authentication.getPrincipal();// 获取凭证也就是用户的手机号
        String phone = "";
        if (principal instanceof UserDetails) {
            phone = ((UserDetails)principal).getUsername();
        } else if (principal instanceof AuthenticatedPrincipal) {
            phone = ((AuthenticatedPrincipal)principal).getName();
        } else if (principal instanceof Principal) {
            phone = ((Principal)principal).getName();
        } else {
            phone = principal == null ? "" : principal.toString();
        }

        String inputCode = (String) authentication.getCredentials(); // 获取输入的验证码
        // 1. 检验Redis手机号的验证码
        String redisCode = redisTemplate.opsForValue().get(phone);
        if (StringUtils.isEmpty(redisCode)) {
            throw new BadCredentialsException("验证码已经过期或尚未发送，请重新发送验证码");
        }
        if (!inputCode.equals(redisCode)) {
            throw new BadCredentialsException("输入的验证码不正确，请重新输入");
        }
        // 2. 根据手机号查询用户信息, 这里演示，直接查了user的信息
        UserDetails userDetails = userService.loadUserByUsername(phone);
        if (userDetails == null) {
            throw new InternalAuthenticationServiceException("phone用户不存在，请注册");
        }

        // 3. 重新创建已认证对象,
        SmsAuthenticationToken authenticationResult = new SmsAuthenticationToken(userDetails.getUsername(), inputCode, userDetails.getAuthorities());
        authenticationResult.setDetails(authenticationToken.getDetails());
        return authenticationResult;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return SmsAuthenticationToken.class.isAssignableFrom(aClass);
    }


}

