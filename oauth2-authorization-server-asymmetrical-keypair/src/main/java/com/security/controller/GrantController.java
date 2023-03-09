package com.security.controller;

import com.security.enums.CodeEnum;
import com.security.model.ClientDetailForm;
import com.security.model.LoginRegisterForm;
import com.security.pojo.ClientDetail;
import com.security.service.ClientService;
import com.security.service.OauthService;
import com.security.service.UserService;
import com.security.utils.RedisUtil;
import com.security.utils.ResponseResult;
import com.sun.net.httpserver.Headers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Controller
@SessionAttributes("authorizationRequest")
@RequestMapping("/auth")
public class GrantController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private UserService userService;

    @Autowired
    private OauthService oauthService;

    @Autowired
    private RedisUtil redisUtil;

    @PostMapping("/register")
    @ResponseBody
    public ResponseResult registerUser(@RequestBody LoginRegisterForm form) {

        userService.registerUser(form);

        return ResponseResult.builder().code(CodeEnum.SUCCESS.getCode()).message(CodeEnum.SUCCESS.getMessage()).build();
    }

    /**
     * 注册oauth2的客户端
     *
     * @return
     */
    @PostMapping("/registerClient")
    @ResponseBody
    public ResponseResult registerClient(@Valid @RequestBody ClientDetailForm clientDetailForm) throws Exception {

        ClientDetail clientDetail = clientDetailForm.toClientDetail(clientDetailForm);

        clientService.addClient(clientDetail);

        return ResponseResult.builder().ok().build();
    }

    /**
     * 自定义授权页面thymeleaf
     *
     * @param request
     * @param modelAndView
     * @return
     * @throws Exception
     */
    @RequestMapping("/confirm_access")
    public ModelAndView getAccessConfirmation(Map<String, Object> request, ModelAndView modelAndView) throws Exception {
        AuthorizationRequest authorizationRequest = (AuthorizationRequest) request.get("authorizationRequest");

        Map<String, Object> confirmInformation = oauthService.getConfirmInformation(authorizationRequest);

        modelAndView.addObject("clientId", confirmInformation.get("clientId"));
        modelAndView.addObject("principalName", confirmInformation.get("principalName"));
        modelAndView.addObject("scopes", confirmInformation.get("scopes"));
        modelAndView.addObject("state", confirmInformation.get("state"));
        modelAndView.addObject("redirectUri", confirmInformation.get("redirectUri"));

        modelAndView.setViewName("/grant");

        return modelAndView;
    }

    @GetMapping("/getCode")
    public ResponseEntity getCode(@RequestParam String code) {
        return ResponseEntity.ok(code);
    }

    @PostMapping("/token")
    @ResponseBody
    public ResponseResult postAccessToken(HttpServletRequest request, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {

        Map<String, Object> token = oauthService.getToken(request, parameters);


        return ResponseResult.builder().code(CodeEnum.SUCCESS.getCode()).message(CodeEnum.SUCCESS.getMessage()).data(token).build();
    }

    @RequestMapping("/base-login")
    public ModelAndView baseLogin(ModelAndView modelAndView, @RequestParam(required = false) String redirectUrl) {
        modelAndView.setViewName("/base-login");
        modelAndView.addObject("redirectUrl", redirectUrl);

        return modelAndView;
    }

    @PostMapping("/login")
    public ResponseResult login(@RequestParam String username, @RequestParam String password) {
        return ResponseResult.builder().ok().build();
    }

    @RequestMapping("/loginFail")
    public ModelAndView loginFail(ModelAndView modelAndView, @RequestParam(required = false) String message) {
        modelAndView.setViewName("/login-fail");
        modelAndView.addObject("message", URLDecoder.decode(message));

        return modelAndView;
    }

    @RequestMapping("/invalidSession")
    public ModelAndView invalidSession(ModelAndView modelAndView, @RequestParam String redirectUrl) {
        modelAndView.setViewName("/invalid-page");
        modelAndView.addObject("redirectUrl", redirectUrl);

        return modelAndView;
    }

    @GetMapping("/getCaptcha")
    public void getRandomCode(HttpServletResponse response) throws IOException {
        userService.getRandomCode(response);
    }

    @GetMapping("/sendCode")
    @ResponseBody
    public ResponseResult sendCode(String phone) {
        // 1. 获取到手机号
        log.info(phone + "请求获取验证码");
        // 2. 模拟调用短信平台获取验证码，以手机号为KEY，验证码为值，存入Redis，过期时间一分钟
        String code = generateRandomCode(6);
        String key = "code:sms" + ":" + phone;
        redisUtil.set(key, code, 60 * 10);
        String saveCode = (String) redisUtil.get(key);// 缓存中的code
        Long expire = redisUtil.getExpire(key); // 查询过期时间
        // 3. 验证码应该通过短信发给用户，这里直接返回吧
        Map<String, String> result = new HashMap<>();
        result.put("code", saveCode);
        result.put("过期时间", expire + "秒");
        return ResponseResult.builder().code(CodeEnum.SUCCESS.getCode()).message(CodeEnum.SUCCESS.getMessage()).data(result).build();
    }

    private String generateRandomCode(int len) {
        Random random = new Random();
        String result = "";
        for (int i = 0; i < len; i++) {
            result += random.nextInt(10);
        }

        return result;
    }
}
