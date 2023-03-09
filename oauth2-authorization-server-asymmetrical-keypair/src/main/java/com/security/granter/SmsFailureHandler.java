package com.security.granter;

import com.security.utils.ResponseResult;
import com.security.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 失败处理
 */
@Slf4j
@Component
@SuppressWarnings("Duplicates")
@RequiredArgsConstructor
public class SmsFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        System.out.println("AuthenticationException: " + exception);

        ResponseUtils.result(response, ResponseResult.builder().error(exception.getMessage()).build());

//        if (exception instanceof UnapprovedClientAuthenticationException) {
//            ResponseUtils.result(response, ResponseResult.builder().error(exception.getMessage()));
//        } else if (exception instanceof BadCredentialsException) {
//            ResponseUtils.result(response, ResponseResult.builder().error(exception.getMessage()));
//        }
    }
}
