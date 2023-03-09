package com.security.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.DefaultThrowableAnalyzer;
import org.springframework.security.oauth2.common.exceptions.InsufficientScopeException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.web.util.ThrowableAnalyzer;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.io.IOException;

/**
 * @Description: 资源服务器异常自定义捕获, 异常翻译器
 * @Package: com.security.exception.OAuth2ServerWebResponseExceptionTranslator
 */
@Component
public class GlobalOAuth2WebResponseExceptionTranslator implements WebResponseExceptionTranslator<OAuth2Exception> {


    @Override
    public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
        /**
         * 直接抛出异常给HandlerInterceptor处理
         */
        throw e;
    }
}

