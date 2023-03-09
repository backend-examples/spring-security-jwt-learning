package com.security.controller;

import com.security.enums.CodeEnum;
import com.security.model.ClientDetailForm;
import com.security.model.LoginRegisterForm;
import com.security.service.ClientService;
import com.security.service.UserService;
import com.security.utils.ResponseResult;
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
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.*;

@Controller
@SessionAttributes("authorizationRequest")
@RequestMapping("/auth")
public class GrantController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private UserService userService;

    /**
     * 因为项目集成了jwt，不能直接使用TokenStore，它是接口
     */
    @Qualifier("jwtTokenStore")
    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private TokenEndpoint tokenEndpoint;

    @PostMapping("/register")
    @ResponseBody
    public ResponseResult registerUser(@RequestBody LoginRegisterForm form) {

        userService.registerUser(form);

        return ResponseResult.builder().code(CodeEnum.SUCCESS.getCode()).message(CodeEnum.SUCCESS.getMessage());
    }

    /**
     * 注册oauth2的客户端
     * @return
     */
    @PostMapping("/registerClient")
    public ResponseEntity registerClient(@RequestBody ClientDetailForm clientDetailForm) throws Exception {

        clientService.addClient(clientDetailForm);

        return null;
    }

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

        if (clientService.getClient(authorizationRequest.getClientId()) == null) {
            throw new Exception("该客户端不存在");
        }

        ModelAndView view = new ModelAndView();
        view.setViewName("/grant");
        view.addObject("clientId", authorizationRequest.getClientId());
        view.addObject("scopes", authorizationRequest.getScope());
        return view;
    }

    @GetMapping("/getCode")
    public ResponseEntity getCode(@RequestParam String code) {
        return ResponseEntity.ok(code);
    }

    @PostMapping("/token")
    @ResponseBody
    public ResponseResult postAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
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

        if (userAuthentication != null) {
            Collection<? extends GrantedAuthority> authorities = userAuthentication.getAuthorities();
            // 权限信息
            List<String> list = new ArrayList<>();
            for (GrantedAuthority authority : authorities) {
                list.add(authority.getAuthority());
            }

            resultMap.put("authorities", list);
        }


        return ResponseResult.builder().code(CodeEnum.SUCCESS.getCode()).message(CodeEnum.SUCCESS.getMessage()).data(resultMap);
    }

    @RequestMapping("/loginFail")
    public ModelAndView loginFail() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/login-fail");
        modelAndView.addObject("message", "用户名或密码错误");
        return modelAndView;
    }
}
