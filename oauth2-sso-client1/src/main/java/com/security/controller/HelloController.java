package com.security.controller;

import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSON;
import com.security.properties.RedisProperties;
import com.security.utils.Base64Util;
import com.security.utils.CookiesUtils;
import com.security.utils.ResponseResult;
import com.security.utils.RestTemplateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Slf4j
@RestController
public class HelloController {

    @Autowired
    private FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    @Autowired
    private RedisProperties redisProperties;

    @GetMapping("/hello")
    public String hello() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName() + Arrays.toString(authentication.getAuthorities().toArray());
    }

    @GetMapping("/getSession")
    public ResponseResult getSession(HttpServletRequest request) {
        Map<String, Cookie> cookies = CookiesUtils.getCookies(request);
        log.debug("cookies: " + cookies);
        log.debug("cookie: " + cookies.get(redisProperties.getCookieName()).getValue());
        log.debug("sessionId: " + Base64Util.decode(cookies.get(redisProperties.getCookieName()).getValue()));
        log.debug("session: " + sessionRepository.findById(Base64Util.decode(cookies.get(redisProperties.getCookieName()).getValue())).getMaxInactiveInterval().toMillis());

        Session session = sessionRepository.findById(Base64Util.decode(cookies.get(redisProperties.getCookieName()).getValue()));
        Object spring_security_context = session.getAttribute("SPRING_SECURITY_CONTEXT");

//        Object oauth2ClientContext = session.getAttribute("scopedTarget.oauth2ClientContext");

        return ResponseResult.builder().success(spring_security_context);
    }

    /**
     * 返回数据中的details对象中的tokenValue，是访问对应资源服务接口的 bearer token
     * @param authentication
     * @return
     */
    @RequestMapping("/getCurrentUser")
    public ResponseResult getCurrentUser(Authentication authentication) throws IllegalAccessException, IntrospectionException, InvocationTargetException {
        Map<String, Object> resultMap = new HashMap<>();
        String username = (String) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        Map<String, Object> details = JSON.parseObject(JSON.toJSONString(authentication.getDetails()), Map.class);

        String accessToken = (String) details.get("tokenValue");

        resultMap.put("username", username);
        resultMap.put("accessToken", accessToken);
        resultMap.put("authorities", authorities);

        return ResponseResult.builder().success(resultMap);
    }


    @RequestMapping("/toLogin")
    public ResponseResult login(Authentication authentication) {
        Map<String, Object> resultMap = new HashMap<>();
        Object details = authentication.getDetails();

        resultMap.put("details", details);

        return ResponseResult.builder().success(resultMap);
    }

    /**
     * 前端单点登录，走该接口，重定向回前端对应页面
     * @param response
     * @throws IOException
     */
    @RequestMapping("/toSuccess")
    public void success(HttpServletResponse response) throws IOException {
        response.sendRedirect("http://localhost:8080/callback?state=ssoSuccess");
    }

    @RequestMapping("/toFail")
    public void fail(HttpServletResponse response) throws IOException {
        response.sendRedirect("http://localhost:8080/callback?state=ssoFail");
    }

    @GetMapping("/api/logout")
    public HttpEntity authLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {//清除认证
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return ResponseEntity.ok("退出成功");
    }
}
