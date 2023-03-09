package com.security.service.impl;

import com.security.constant.Messages;
import com.security.controller.GrantController;
import com.security.enums.ScopeWithDescriptionEnum;
import com.security.exception.BizException;
import com.security.model.SecurityUser;
import com.security.service.OauthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OauthServiceImpl implements OauthService {

    @Autowired
    private ClientDetailsService clientDetailsService;

    /**
     * 因为项目集成了jwt，不能直接使用TokenStore，它是接口
     */
    @Qualifier("jwtTokenStore")
    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private TokenEndpoint tokenEndpoint;

    @Override
    public Map<String, Object> getConfirmInformation(AuthorizationRequest authorizationRequest) {
        Set<String> scopesToApprove = new LinkedHashSet<>();
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(authorizationRequest.getClientId());

        // 获取登录信息
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        for (String scope:authorizationRequest.getScope()) {
            for (String requestedScope : StringUtils.delimitedListToStringArray(scope, " ")) {
                if (clientDetails.getScope().contains(requestedScope)) {
                    scopesToApprove.add(requestedScope);
                }
            }
        }

        Set<ScopeWithDescriptionEnum> scopeWithDescriptions = ScopeWithDescriptionEnum.withDescription(scopesToApprove);

        Map<String, Object> confirmMap = new HashMap<>();
        confirmMap.put("clientId", clientDetails.getClientId());
        confirmMap.put("principalName", auth.getName());
        confirmMap.put("scopes", scopeWithDescriptions);
        confirmMap.put("state", authorizationRequest.getState());
        confirmMap.put("redirectUri", clientDetails.getRegisteredRedirectUri().iterator().next());

        return confirmMap;
    }

    @Override
    public Map<String, Object> getToken(HttpServletRequest request, Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        ClientDetails client;
        String clientId;

        // 1. 获取客户端认证信息
        String header = request.getHeader("Authorization");
        if (header == null || !header.toLowerCase().startsWith("basic ")) {
            clientId = parameters.get("client_id");
        } else {
            // 解密请求头
            String[] clients = extractAndDecodeHeader(header);
            if (clients.length != 2) {
                throw new BadCredentialsException("Invalid basic authentication token");
            }

            clientId = clients[0];
        }

        // 从数据库获取客户端数据
        client = clientDetailsService.loadClientByClientId(clientId);

        // 将ClientDetails类转为用户类
        SecurityUser securityUser = new SecurityUser();
        securityUser.setUsername(client.getClientId());
        securityUser.setPassword(client.getClientSecret());
        securityUser.setAuthorities(new ArrayList<>());

        // 将客户端类转换为UsernamePasswordAuthenticationToken
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(securityUser, null, new ArrayList<>());

        // 对应授权类型所需的parameters参数要完整
        OAuth2AccessToken accessToken = tokenEndpoint.postAccessToken(usernamePasswordAuthenticationToken, parameters).getBody();

        Map<String, Object> resultMap = new HashMap<>();

        if (accessToken == null) {
            throw new BizException(Messages.LOGIN_FAILURE);
        }

        // token信息
        resultMap.put("access_token", accessToken.getValue());
        resultMap.put("refresh_token", accessToken.getRefreshToken());
        resultMap.put("token_type", accessToken.getTokenType());
        resultMap.put("expires_in", accessToken.getExpiresIn());
        resultMap.put("scope", accessToken.getScope().toArray(new String[0]));
        resultMap.putAll(accessToken.getAdditionalInformation());

        OAuth2Authentication authentication = tokenStore.readAuthentication(accessToken);
        Authentication userAuthentication = authentication.getUserAuthentication();
        Collection<? extends GrantedAuthority> authorities = userAuthentication.getAuthorities();
        // 权限信息
        List<String> list = new ArrayList<>();
        for (GrantedAuthority authority : authorities) {
            list.add(authority.getAuthority());
        }

        resultMap.put("authorities", list);

        return resultMap;
    }

    /**
     * 对请求头进行bas64解码以及解析
     *
     * @param header
     * @return
     */
    private String[] extractAndDecodeHeader(String header) {
        byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(base64Token);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException(
                    "Failed to decode basic authentication token");
        }
        String token = new String(decoded, StandardCharsets.UTF_8);
        int delimiter = token.indexOf(":");

        if (delimiter == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        }
        return new String[]{token.substring(0, delimiter), token.substring(delimiter + 1)};
    }
}
