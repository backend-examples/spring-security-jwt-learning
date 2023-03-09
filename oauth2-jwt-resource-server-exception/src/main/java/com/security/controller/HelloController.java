package com.security.controller;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HelloController {

    @Resource
    private TokenStore tokenStore;

    @RequestMapping("/hello")
    @ResponseBody
    public Map<String, Object> hello(OAuth2Authentication authentication) {
        Map<String, Object> map = getExtraInfo(authentication);
        return map;
    }

    public Map<String, Object> getExtraInfo(OAuth2Authentication auth) {
        OAuth2AuthenticationDetails details
                = (OAuth2AuthenticationDetails) auth.getDetails();
        OAuth2AccessToken accessToken = tokenStore
                .readAccessToken(details.getTokenValue());
        return accessToken.getAdditionalInformation();
    }



}
