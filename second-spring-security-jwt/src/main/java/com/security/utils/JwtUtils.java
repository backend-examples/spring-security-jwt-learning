package com.security.utils;

import com.security.constant.RedisConstant;
import com.security.constant.SecurityConstants;
import com.security.enums.CodeEnum;
import com.security.exception.BizException;
import com.security.model.SecurityUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author  不才陈某
 * JWT的工具类
 */
@Component
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtUtils {

    //秘钥
    private String secret;

    // 过期时间 毫秒
    private Long expiration;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 校验uuid，不一致则是重新登录
     * @param securityUser
     * @return
     */
    public boolean checkUUID (SecurityUser securityUser) {
        String uuid = (String) redisUtil.get(RedisConstant.UNIQUE_USER_PREFIX+securityUser.getUsername());


        if (uuid != null && !uuid.equals(securityUser.getUuid())) {
            return true;
        }

        return false;
    }

    public String createToken(String userName, boolean isRememberMe) {
        long expiration = isRememberMe ? SecurityConstants.EXPIRATION_REMEMBER : SecurityConstants.EXPIRATION;

        return Jwts.builder()
                .setSubject(userName)
                //生成时间
                .setIssuedAt(new Date())
                //过期时间
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String createToken(SecurityUser securityUser, boolean isRememberMe){
        long expiration = isRememberMe ? SecurityConstants.EXPIRATION_REMEMBER : SecurityConstants.EXPIRATION;

        Map<String, Object> claims = new HashMap<>();
        securityUser.setUuid(IdUtils.fastUUID());
        claims.put("userId", securityUser.getId());
        claims.put("username", securityUser.getUsername());
        claims.put("roles", securityUser.getAuthorities());
        claims.put("uuid", securityUser.getUuid());
        claims.put("loginTime", securityUser.getLoginTime());
        claims.put("expireTime", securityUser.getExpireTime());

        // 过期时间
        Date expireTime = new Date(new Date().getTime() + expiration * 1000);

        // 生成token
        String accessToken = Jwts.builder()
                .addClaims(claims)
                //生成时间
                .setIssuedAt(new Date())
                //过期时间
                .setExpiration(expireTime)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();

        redisUtil.set(RedisConstant.UNIQUE_USER_PREFIX+securityUser.getUsername(), securityUser.getUuid(), expiration);

        return accessToken;
    }

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String generateToken(Map<String, Object> claims,Long expiration) {
        Date expirationDate = new Date(System.currentTimeMillis() + expiration);
        return Jwts.builder().setClaims(claims).setExpiration(expirationDate).signWith(SignatureAlgorithm.HS512, secret).compact();
    }


    /**
     * 从token中解析出数据
     * @param token 令牌
     * @return
     */
    private Claims getClaimsFromToken(String token) {
        Claims claims = null;
        claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        return claims;
    }

    /**
     * 从令牌中获取用户名
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        String username = null;
        username = (String) getClaimsFromToken(token).get("username");
        return username;
    }

    /**
     * 获取token负载中的uuid,用于和缓存中的uuid比较，校验当前登录用户是否已登录
     * @param token
     * @return
     */
    public String getUUIDFromToken(String token) {
        String uuid = null;
        uuid = (String) getClaimsFromToken(token).get("uuid");
        return uuid;
    }

    public Long getExpireTimeFromToken(String token) {
        long expireTime = 0L;
        expireTime = (long) getClaimsFromToken(token).get("expireTime");

        return expireTime;
    }

    /**
     * 判断令牌是否过期
     *
     * @param token 令牌
     * @return 是否过期
     */
    public Boolean isTokenExpired(String token) throws ExpiredJwtException {
        Claims claims = getClaimsFromToken(token);
        Date expiration = claims.getExpiration();

        boolean before = expiration.before(new Date());

        return before;
    }

    /**
     * 刷新令牌
     *
     * @param token 原令牌
     * @return 新令牌
     */
    public String refreshToken(String token, boolean isRememberMe) {
        long expiration = isRememberMe ? SecurityConstants.EXPIRATION_REMEMBER : SecurityConstants.EXPIRATION;
        String refreshedToken;
        try {
            Claims claims = getClaimsFromToken(token);
            claims.put(Claims.ISSUED_AT, new Date());
            refreshedToken = generateToken(claims,2*expiration);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    /**
     * 验证令牌
     *
     * @param token       令牌
     * @param userDetails 用户
     * @return 是否有效
     */
    public Boolean validateToken(String token, UserDetails userDetails) {

        SecurityUser user = (SecurityUser) userDetails;
        String username = getUsernameFromToken(token);

        return username.equals(user.getUsername());
    }


}
