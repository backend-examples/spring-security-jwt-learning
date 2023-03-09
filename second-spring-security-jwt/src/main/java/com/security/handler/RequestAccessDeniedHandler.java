package com.security.handler;

import com.security.enums.CodeEnum;
import com.security.utils.ResponseResult;
import com.security.utils.ResponseUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * 当认证后的用户访问受保护的资源时，权限不够，则会进入这个处理器
 */
@Component
public class RequestAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        ResponseUtils.result(response, ResponseResult.builder().code(CodeEnum.FORBIDDEN.getCode()).message(CodeEnum.FORBIDDEN.getMessage()));
    }
}