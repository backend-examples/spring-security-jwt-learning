package com.security.service.impl;

import com.security.service.AuthorizationCodeService;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthorizationCodeServiceImpl implements AuthorizationCodeService {

    protected final ConcurrentHashMap<String, OAuth2Authentication> authorizationCodeStore = new ConcurrentHashMap();

    // 生成随机字符的类
    private RandomValueStringGenerator generator = new RandomValueStringGenerator(10);

    /**
     * @description 生成授权码的方法
     * @param: [oAuth2Authentication]
     * @return: java.lang.String
     */
    @Override
    public String createAuthorizationCode(OAuth2Authentication authentication) {
        String code = this.generator.generate();
        authorizationCodeStore.put(code, authentication);

        return code;
    }

    @Override
    public OAuth2Authentication consumeAuthorizationCode(String code) throws InvalidGrantException {
        OAuth2Authentication authentication = authorizationCodeStore.remove(code);

        return authentication;
    }
}
