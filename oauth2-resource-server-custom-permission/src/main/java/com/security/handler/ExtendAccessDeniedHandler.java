package com.security.handler;

import com.security.utils.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.common.exceptions.InsufficientScopeException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class ExtendAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(ExtendAccessDeniedHandler.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {

        Throwable cause = accessDeniedException.getCause();

        Map<String, Object> map = new HashMap<>(4);
        String message;

        if (cause instanceof InsufficientScopeException) {

            InsufficientScopeException insufficientScopeException = (InsufficientScopeException) cause;
            Map<String, String> additionalInformation = insufficientScopeException.getAdditionalInformation();

            String scope = additionalInformation.get("scope");

            message = String.format("所需scope为: %s", scope);

        } else {
            message = accessDeniedException.getMessage();
        }

        map.put("message", message);
        ResponseUtils.response(response, map);
    }
}

