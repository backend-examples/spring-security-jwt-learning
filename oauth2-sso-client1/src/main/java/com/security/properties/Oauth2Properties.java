package com.security.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Data
@Component
@PropertySource("classpath:application.yml")
public class Oauth2Properties {

    @Value("${oauth2.client.client-id}")
    private String clientId;

    @Value("${oauth2.client.client-secret}")
    private String clientSecret;

    @Value("${oauth2.client.user-authorization-uri}")
    private String userAuthorizationUri;

    @Value("${oauth2.client.access-token-uri}")
    private String accessTokenUri;

    @Value("${oauth2.client.check-token-uri}")
    private String checkTokenUri;

    @Value("${oauth2.client.scope}")
    private List<String> scopes;

    @Value("${oauth2.client.use-current-uri}")
    private boolean useCurrentUri;

    @Value("${oauth2.client.pre-established-redirect-uri}")
    private String preEstablishedRedirectUri;

    @Value("${oauth2.resource.user-info-uri}")
    private String userInfoUri;

    @Value("${oauth2.resource.jwt.key-uri}")
    private String keyUri;

    @Value("${oauth2.resource.jwt.key-value}")
    private String keyValue;
}
