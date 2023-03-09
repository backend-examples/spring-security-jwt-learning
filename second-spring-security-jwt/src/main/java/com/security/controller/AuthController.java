package com.security.controller;

import cn.hutool.core.util.ObjectUtil;
import com.security.enums.CodeEnum;
import com.security.model.LoginRegisterForm;
import com.security.model.SecurityUser;
import com.security.service.UserService;
import com.security.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseResult registerUser(@RequestBody LoginRegisterForm form) {

        userService.registerUser(form);

        return ResponseResult.builder().code(CodeEnum.SUCCESS.getCode()).message(CodeEnum.SUCCESS.getMessage());
    }

    @PostMapping("/checkLogin")
    public ResponseResult checkLogin(@RequestBody LoginRegisterForm form) {
        UserDetails userDetails = userService.loadUserByUsername(form.getUsername());
        userService.checkLogin((SecurityUser) userDetails);

        return ResponseResult.builder().code(CodeEnum.SUCCESS.getCode());
    }

    @PostMapping("/login")
    public ResponseResult login(@RequestBody LoginRegisterForm form) {

        Map<String, Object> token = userService.login(form);

        System.out.println("securityUser: " + token);

        return ResponseResult.builder().code(CodeEnum.SUCCESS.getCode()).message(CodeEnum.SUCCESS.getMessage()).data(token);
    }

    //randomCode是一个时间戳，由前端生成后请求后端，具体是防止redis中的key重复
    @GetMapping("/getRandomCode")
    public void getRandomCode(HttpServletResponse response) throws IOException {
        userService.getRandomCode(response);
    }
}
