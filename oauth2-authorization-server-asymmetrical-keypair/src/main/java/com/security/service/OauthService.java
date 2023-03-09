package com.security.service;

import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface OauthService {

    Map<String, Object> getConfirmInformation(AuthorizationRequest authorizationRequest);

    Map<String, Object> getToken(HttpServletRequest request, Map<String, String> parameters) throws HttpRequestMethodNotSupportedException;
}
