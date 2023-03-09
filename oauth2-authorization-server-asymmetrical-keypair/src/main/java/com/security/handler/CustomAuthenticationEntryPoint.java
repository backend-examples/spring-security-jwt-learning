package com.security.handler;

import com.security.utils.ResponseResult;
import com.security.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.error("Authentication异常: [{}], [{}], [{}]", request.getRequestURI(), exception.getMessage(), exception);

        ResponseUtils.result(response, ResponseResult.builder().error(401, exception.getMessage()).build());
    }
}

