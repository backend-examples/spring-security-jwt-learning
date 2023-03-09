package com.security.controller;

import com.sun.deploy.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;

@Controller
@SessionAttributes("authorizationRequest")
@RequestMapping("/auth")
public class GrantController {

    /**
     * 因为项目集成了jwt，不能直接使用TokenStore，它是接口
     */
    @Qualifier("jwtTokenStore")
    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private TokenEndpoint tokenEndpoint;

    /**
     * 自定义授权页面thymeleaf
     *
     * @param model
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/confirm_access")
    public ModelAndView getAccessConfirmation(Map<String, Object> model, HttpServletRequest request) throws Exception {
        AuthorizationRequest authorizationRequest = (AuthorizationRequest) model.get("authorizationRequest");
        ModelAndView view = new ModelAndView();
//        view.setViewName("/html/grant.html"); // 没使用thymeleaf，且未配置视图解析器，在这里需要使用完整路径
        view.setViewName("/grant"); // 使用thymeleaf，grant前面不能少了`/`，且已经在application.yml中配置了thymeleaf的视图解析器
        view.addObject("clientId", authorizationRequest.getClientId());
        view.addObject("scopes", authorizationRequest.getScope());
        return view;
    }

    /**
     * 自定义/oauth/token的逻辑
     * @param principal
     * @param parameters
     * @return
     * @throws HttpRequestMethodNotSupportedException
     */
    @PostMapping("/token")
    public ResponseEntity postAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        OAuth2AccessToken accessToken = tokenEndpoint.postAccessToken(principal, parameters).getBody();

        Map<String, Object> resultMap = new HashMap<>();

        // token信息
        resultMap.put("access_token", accessToken.getValue());
        resultMap.put("refresh_token", accessToken.getRefreshToken());
        resultMap.put("token_type", accessToken.getTokenType());
        resultMap.put("expires_in", accessToken.getExpiresIn());
        resultMap.put("scope", StringUtils.join(accessToken.getScope(), ","));
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


        return ResponseEntity.ok(resultMap);
    }

    /**
     * base-login.html由模板转成springboot能识别的静态资源
     * @param model
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/base-login")
    public ModelAndView login(Map<String, Object> model, HttpServletRequest request) throws Exception {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("/base-login");

        return modelAndView;
    }
}