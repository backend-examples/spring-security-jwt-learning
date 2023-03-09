package com.security.handler;

import com.security.constant.Messages;
import com.security.enums.CodeEnum;
import com.security.utils.ResponseResult;
import com.security.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户访问受保护的资源，但是用户没有认证则会进入这个处理器
 * 屏蔽默认登录页面，即当用户未登录时，不自动跳转到默认的登录页，而是返回用户未登录的错误。用于前后端分离场景
 * 返回json数据，阻止重定向到默认登录页
 */
@Component
@Slf4j
public class EntryPointUnauthorizedHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        ResponseUtils.result(response, ResponseResult.builder().code(CodeEnum.NOT_AUTHENTICATION.getCode()).message(Messages.NOT_AUTHENTICATION));
    }
}
