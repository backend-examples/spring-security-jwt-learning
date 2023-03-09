package com.security.filter;

import com.alibaba.fastjson2.JSON;
import com.security.constant.Messages;
import com.security.constant.RedisConstant;
import com.security.constant.SecurityConstants;
import com.security.enums.CodeEnum;
import com.security.exception.BizException;
import com.security.model.SecurityUser;
import com.security.service.UserService;
import com.security.utils.JwtUtils;
import com.security.utils.RedisUtil;
import com.security.utils.ResponseResult;
import com.security.utils.ResponseUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * 校验token的过滤器，直接获取header中的token进行校验，
 */
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    /**
     * JWT的工具类
     */
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * UserDetailsService的实现类，从数据库中加载用户详细信息
     */
    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String token = request.getHeader(SecurityConstants.TOKEN_HEADER);

        /**
         * token存在则校验token
         * 1. token是否存在
         * 2. token存在：
         *  2.1 校验token中的用户名是否失效
         */
        if (!StringUtils.isEmpty(token)) {
            // 先验证token是否过期
            try {
                String username = jwtUtils.getUsernameFromToken(token);

                // SecurityContextHolder.getContext().getAuthentication()==null 未认证则为true
                if (!StringUtils.isEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    SecurityUser securityUser = (SecurityUser) userService.loadUserByUsername(username);
                    securityUser.setUuid(jwtUtils.getUUIDFromToken(token));
                    securityUser.setExpireTime(jwtUtils.getExpireTimeFromToken(token));

                    // 如果token在黑名单中，则禁止访问
                    if  (redisUtil.get(RedisConstant.TOKEN_JTI+username) != null) {
                        ResponseUtils.result(response, ResponseResult.builder().code(CodeEnum.LOGIN_EXITED.getCode()).message(CodeEnum.LOGIN_EXITED.getMessage()));

                        // 直接继续执行下一个过滤器
                        chain.doFilter(request,response);
                    }

                    // 校验uuid，如果已登录，则将当前token加入黑名单
                    if (jwtUtils.checkUUID(securityUser)) {
                        // 计算还剩多少过期时间，并加入黑名单
                        if (!jwtUtils.isTokenExpired(token)) {
                            long leaveExpireTime = ((securityUser).getExpireTime() - new Date().getTime()) / 1000;
                            System.out.println("leaveExpireTime: " + leaveExpireTime);

                            redisUtil.set(RedisConstant.TOKEN_JTI+username, (securityUser).getUuid(), leaveExpireTime);
                        }

                        ResponseUtils.result(response, ResponseResult.builder().code(CodeEnum.LOGIN_EXITED.getCode()).message(CodeEnum.LOGIN_EXITED.getMessage()));

                        // 提前执行下一个过滤器
                        chain.doFilter(request, response);
                    }

                    /**
                     * 由于通过controller自定义登录逻辑
                     * 所以在这里必须通过spring-security的Filter将用户认证信息存储好
                     * 否则spring-security框架不知道用户是否已登录
                     * 导致登录完成之后，无法访问其他接口
                     */
                    if (jwtUtils.validateToken(token, securityUser)){
                        // 将用户信息存入 authentication，方便后续校验
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(securityUser, null,
                                securityUser.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        // 将 authentication 存入 ThreadLocal，方便后续获取用户信息
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                if (e instanceof ExpiredJwtException) {
                    e.printStackTrace();
//                    String username = String.valueOf(((ExpiredJwtException) e).getClaims());
                    ResponseUtils.result(response, ResponseResult.builder().code(CodeEnum.ACCOUNT_EXPIRED.getCode()).message(CodeEnum.ACCOUNT_EXPIRED.getMessage()));
                }

                // token签名验证不通过
                if (e instanceof SignatureException) {
                    e.printStackTrace();

                    ResponseUtils.result(response, ResponseResult.builder().code(CodeEnum.SIGNATURE_NOT_MATCH.getCode()).message(CodeEnum.SIGNATURE_NOT_MATCH.getMessage()));
                }
            }
        }

        //继续执行下一个过滤器
        chain.doFilter(request,response);
    }
}
