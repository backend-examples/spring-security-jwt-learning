package com.security.filter;

import com.security.handler.ValidateCodeProcessorHandler;
import com.security.utils.ResponseResult;
import com.security.utils.ResponseUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 验证码过滤器。
 *
 * <p>继承于 {@link OncePerRequestFilter} 确保在一次请求只通过一次filter</p>
 * <p>需要配置指定拦截路径，默认拦截 POST 请求</p>
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/7/28 下午11:15
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ValidateCodeFilter extends OncePerRequestFilter {

    private final @NonNull ValidateCodeProcessorHandler validateCodeProcessorHandler;
    private Map<String, String> urlMap = new HashMap<>();
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        // 路径拦截
        urlMap.put("/oauth/sms", "sms");
        urlMap.put("/oauth/email", "email");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String validateCodeType = getValidateCodeType(request);
        if (!StringUtils.isEmpty(validateCodeType)) {
            try {
                log.info("请求需要验证！验证请求：" + request.getRequestURI() + " 验证类型：" + validateCodeType);
                validateCodeProcessorHandler.findValidateCodeProcessor(validateCodeType)
                        .validate(new ServletWebRequest(request, response));
            } catch (Exception e) {
                e.printStackTrace();
                ResponseUtils.result(response, ResponseResult.builder().error(e.getMessage()).build());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getValidateCodeType(HttpServletRequest request) {
        if (HttpMethod.POST.matches(request.getMethod())) {
            Set<String> urls = urlMap.keySet();
            for (String url : urls) {
                // 如果路径匹配，就回去他的类型，也就是 map 的 value
                log.info("request.getRequestURI(): " + request.getRequestURI() + " >>> context path: " + request.getContextPath() + " >>> replace: " + request.getRequestURI().replaceFirst(request.getContextPath(), ""));
                if (antPathMatcher.match(url, request.getRequestURI().replaceFirst(request.getContextPath(), ""))) {
                    return urlMap.get(url);
                }
            }
        }

        return null;
    }
}
