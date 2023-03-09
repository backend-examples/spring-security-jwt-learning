package com.security.controller;

import com.security.enums.CodeEnum;
import com.security.model.SecurityUser;
import com.security.utils.ResponseResult;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping("/user")
public class UserController {

    @Secured("ROLE_user")
    @GetMapping("/getUserInfo")
    public ResponseResult getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUser user = (SecurityUser) authentication.getPrincipal();
        return ResponseResult.builder().code(CodeEnum.SUCCESS.getCode()).message(CodeEnum.SUCCESS.getMessage()).data(user);
    }

    @RolesAllowed("ROLE_user")
    @GetMapping("/admin/testA")
    public String testA() {
        return "这是管理员能查看的";
    }

    @RolesAllowed("ROLE_admin")
    @GetMapping("/user/testU")
    public String testU() {
        return "这是登录用户能查看的";
    }

    @RolesAllowed({"ROLE_user", "admin"})
    @GetMapping("/testC")
    public String testC() {
        return "这是admin和user都能查看的";
    }

    @PreAuthorize("hasRole('user')")
    @GetMapping("/testD")
    public String testD() {
        return "这是user都能查看的";
    }

    @PreAuthorize("#userId == 1 && hasRole('user') ")
    @GetMapping("/testE")
    public String changePassword(Integer userId){
        return "PreAuthorize 表达式";
    }

    /**
     * principal.id: 当前token用户的id
     * @param userId
     * @return
     */
    @PreAuthorize("#userId == authentication.principal.id ")
    @GetMapping("/testF")
    public String testF(Integer userId){
        return "PreAuthorize 表达式";
    }

    /**
     * principal.username: 当前用户的username
     * @param username
     * @return
     */
    @PreAuthorize("#username == authentication.principal.username ")
    @GetMapping("/testG")
    public String testG(String username){
        return "PreAuthorize 表达式";
    }

    /**
     * @sc是我的自定义鉴权服务名
     * hasPermission(#username)：服务内方法名称和参数
     * 传参方式需要采用SpEL表达式支持的语法，必须使用#开头，后面接参数名
     * @param username
     * @return
     */
    @PreAuthorize("@sc.hasPermission(#username)")
    @GetMapping("/testH")
    public String testH(String username){
        return "PreAuthorize 表达式";
    }
}
