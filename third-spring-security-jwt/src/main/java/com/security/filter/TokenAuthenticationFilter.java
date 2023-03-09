package com.security.filter;

import com.security.constant.SecurityConstant;
import com.security.service.UserService;
import com.security.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

    /**
     * UserDetailsService的实现类，从数据库中加载用户详细信息
     */
    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String token = request.getHeader(SecurityConstant.TOKEN_HEADER);

        /**
         * token存在则校验token
         * 1. token是否存在
         * 2. token存在：
         *  2.1 校验token中的用户名是否失效
         */
        if (!StringUtils.isEmpty(token)){
            String username = jwtUtils.getUsernameFromToken(token);
            // SecurityContextHolder.getContext().getAuthentication()==null 未认证则为true
            if (!StringUtils.isEmpty(username) && SecurityContextHolder.getContext().getAuthentication()==null){
                UserDetails userDetails = userService.loadUserByUsername(username);
                // 如果token有效

                /**
                 * 由于通过controller自定义登录逻辑
                 * 所以在这里必须通过spring-security的Filter将用户认证信息存储好
                 * 否则spring-security框架不知道用户是否已登录
                 * 导致登录完成之后，无法访问其他接口
                 */
                if (jwtUtils.validateToken(token, userDetails)){
                    // 将用户信息存入 authentication，方便后续校验
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                            userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // 将 authentication 存入 ThreadLocal，方便后续获取用户信息
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        //继续执行下一个过滤器
        chain.doFilter(request,response);
    }
}
