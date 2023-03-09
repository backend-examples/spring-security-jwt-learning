package com.security.controller;

import com.security.enums.CodeEnum;
import com.security.model.ClientDetailForm;
import com.security.model.LoginRegisterForm;
import com.security.model.LoginResp;
import com.security.model.SecurityUser;
import com.security.pojo.ClientDetail;
import com.security.service.ClientService;
import com.security.service.UserService;
import com.security.utils.RedisUtil;
import com.security.utils.ResponseResult;
import com.sun.deploy.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.BeanIds;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.endpoint.AbstractEndpoint;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.*;

@Controller
@SessionAttributes("authorizationRequest")
@RequestMapping("/auth")
@Slf4j
public class GrantController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientDetailsService clientDetailsService;

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

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisUtil redisUtil;

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
    @ResponseBody
    public ResponseResult registerClient(@Valid @RequestBody ClientDetailForm clientDetailForm) throws Exception {

        ClientDetail clientDetail = clientDetailForm.toClientDetail(clientDetailForm);

        clientService.addClient(clientDetail);

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

    /**
     * 重新实现获取token的逻辑，不再将框架自带的/oauth/token接口返回token的逻辑引入到这里
     * 因为单点需要框架自带的的/oauth/token接口，否则会报错。
     * 该接口调用了框架自带的postAccessToken获取对应token并自定义返回格式。
     * @param parameters
     * @return
     * @throws HttpRequestMethodNotSupportedException
     */
    @PostMapping("/token")
    @ResponseBody
    public ResponseResult postAccessToken(@RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        // 从数据库获取客户端数据
        ClientDetails client = clientDetailsService.loadClientByClientId(parameters.get("client_id"));
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


        return ResponseResult.builder().code(CodeEnum.SUCCESS.getCode()).message(CodeEnum.SUCCESS.getMessage()).data(resultMap);
    }

    @RequestMapping("/loginFail")
    public ModelAndView loginFail() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/login-fail");
        modelAndView.addObject("message", "用户名或密码错误");
        return modelAndView;
    }

    @PostMapping(value = "/login")
    public ResponseResult login(@RequestBody LoginRegisterForm req) throws HttpRequestMethodNotSupportedException {
        return null;
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
        redisUtil.set(phone, code, 60*10);
        String saveCode = (String) redisUtil.get(phone);// 缓存中的code
        Long expire = redisUtil.getExpire(phone); // 查询过期时间
        // 3. 验证码应该通过短信发给用户，这里直接返回吧
        Map<String,String> result=new HashMap<>();
        result.put("code",saveCode);
        result.put("过期时间",expire+"秒");
        return ResponseResult.builder().code(CodeEnum.SUCCESS.getCode()).message(CodeEnum.SUCCESS.getMessage()).data(result);
    }

    private String generateRandomCode(int len) {
        Random random = new Random();
        String result="";
        for (int i=0;i<len;i++)
        {
            result+=random.nextInt(10);
        }

        return result;
    }

}
