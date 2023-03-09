package com.security.handler;

import com.security.utils.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class ExtendAuthenticationEntryPointHandler extends OAuth2AuthenticationEntryPoint {

    private static final Logger log = LoggerFactory.getLogger(ExtendAuthenticationEntryPointHandler.class);

    // 获取token
    private final TokenExtractor TOKEN_EXTRACTOR = new BearerTokenExtractor();

    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Autowired
    public void setJwtAccessTokenConverter(JwtAccessTokenConverter jwtAccessTokenConverter) {
        this.jwtAccessTokenConverter = jwtAccessTokenConverter;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, IOException {

        Throwable cause = authException.getCause();

        Map<String, Object> map = new HashMap<>(4);
        map.put("detailMessage", authException.getMessage());

        String message;

        if (cause instanceof OAuth2AccessDeniedException) {
            message = "资源ID不在resource_ids范围内";
        }  else if (cause instanceof InvalidTokenException) {
            // 获取认证用户信息
            Authentication authentication = TOKEN_EXTRACTOR.extract(request);
            String token = (String) authentication.getPrincipal();

            // jwt token缓存
            TokenStore tokenStore = new JwtTokenStore(jwtAccessTokenConverter);

            try {
                OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(token);

                if (oAuth2AccessToken.isExpired()) {
                    message = "Token已过期";
                } else {
                    message = "token解析失败";
                }
            } catch (Exception e) {
                message = "非法token";
            }
        } else if (authException instanceof InsufficientAuthenticationException) {
            message = "未携带token";
        } else {
            message = "未知异常信息";
        }

        map.put("message", message);
        ResponseUtils.response(response, map);
    }

}

