package com.security.controller;

import com.security.enums.CodeEnum;
import com.security.model.LoginRegisterForm;
import com.security.service.JwtTokenUserDetailsServiceImpl;
import com.security.utils.JwtUtils;
import com.security.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("")
public class UserController {

    @Autowired
    private JwtTokenUserDetailsServiceImpl jwtTokenUserDetailsService;

    @Autowired
    private JwtUtils jwtUtils;
    
    @GetMapping("/getUserInfo")
    public ResponseResult getUserInfo(@RequestParam String username) {
        return ResponseResult.builder().code(CodeEnum.SUCCESS.getCode());
    }

    @GetMapping("/admin/testA")
    public String testA() {
        return "这是管理员能查看的";
    }

    @GetMapping("/user/testU")
    public String testU() {
        return "这是登录用户能查看的";
    }
}
