package com.security.controller;

import com.security.enums.CodeEnum;
import com.security.model.LoginRegisterForm;
import com.security.service.UserService;
import com.security.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/login")
    public ResponseResult login(@RequestBody LoginRegisterForm form) {

        Map<String, Object> token = userService.login(form);

        System.out.println("securityUser: " + token);

        return ResponseResult.builder().code(CodeEnum.SUCCESS.getCode()).message(CodeEnum.SUCCESS.getMessage()).data(token);
    }
}
