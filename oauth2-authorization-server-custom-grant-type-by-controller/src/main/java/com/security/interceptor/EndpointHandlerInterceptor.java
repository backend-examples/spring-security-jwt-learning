package com.security.interceptor;

import com.security.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.springframework.security.oauth2.common.exceptions.UnsupportedResponseTypeException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务器的异常处理器
 */
@Slf4j
public class EndpointHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("EndpointHandlerInterceptor afterCompletion");

        Map<String, Object> map = new HashMap<>(4);

        // 处理client_id不存在的异常
        if (ex instanceof InvalidClientException) {
            String clientId = request.getParameter("client_id");
            map.put("msg", String.format("client id 无效: %s", clientId));

            ResponseUtils.response(response, map);
        } else if (ex instanceof NoSuchClientException) {
            String clientId = request.getParameter("client_id");
            map.put("msg", String.format("client id 不存在: %s", clientId));

            ResponseUtils.response(response, map);
        } else if (ex instanceof RedirectMismatchException) {
            String redirectUri = request.getParameter("redirect_uri");
            map.put("msg", String.format("redirect_uri 不匹配: %s", redirectUri));

            ResponseUtils.response(response, map);
        } else if (ex instanceof UnsupportedResponseTypeException) {
            String responseType = request.getParameter("response_type");
            map.put("msg", String.format("response_type 不支持: %s", responseType));

            ResponseUtils.response(response, map);
        } else if (ex instanceof InvalidScopeException) {
            String scope = request.getParameter("scope");
            map.put("msg", String.format("scope 不支持: %s", scope));

            ResponseUtils.response(response, map);
        }
    }
}
