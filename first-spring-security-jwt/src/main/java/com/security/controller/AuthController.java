package com.security.controller;

import com.security.enums.CodeEnum;
import com.security.model.LoginRegisterForm;
import com.security.service.JwtTokenUserDetailsServiceImpl;
import com.security.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtTokenUserDetailsServiceImpl jwtTokenUserDetailsService;

    @PostMapping("/register")
    public ResponseResult register(@RequestBody LoginRegisterForm form) {
        jwtTokenUserDetailsService.registerUser(form);

        Map<String, Object> map = new HashMap<>();
        return ResponseResult.builder().code(CodeEnum.SUCCESS.getCode()).data(map);
    }
}
