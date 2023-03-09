package com.security.granter;

import com.security.constant.RedisConstant;
import com.security.exception.BizException;
import com.security.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.util.Assert;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 验证码授权模式 授权者
 *
 * @author <a href="mailto:xianrui0365@163.com">xianrui</a>
 * @date 2021/9/25
 */
public class CaptchaTokenGranter extends AbstractTokenGranter {

    private final RedisUtil redisUtil;

    /**
     * 声明授权者 CaptchaTokenGranter 支持授权模式 captcha
     * 根据接口传值 grant_type = captcha 的值匹配到此授权者
     * 匹配逻辑详见下面的两个方法
     *
     * @see CompositeTokenGranter#grant(String, TokenRequest)
     * @see AbstractTokenGranter#grant(String, TokenRequest)
     */
    private static final String GRANT_TYPE = "captcha";
    private final AuthenticationManager authenticationManager;

    public CaptchaTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
                               OAuth2RequestFactory requestFactory, AuthenticationManager authenticationManager,
                               RedisUtil redisUtil
    ) {
        super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
        this.authenticationManager = authenticationManager;
        this.redisUtil = redisUtil;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {

        Map<String, String> parameters = new LinkedHashMap(tokenRequest.getRequestParameters());

        // 验证码校验逻辑
        String validateCode = parameters.get("validateCode");
        String uuid = parameters.get("uuid");

        Assert.isTrue(StringUtils.isNotBlank(validateCode), "验证码不能为空");

        // 从缓存取出正确的验证码和用户输入的验证码比对
        String correctValidateCode = (String) redisUtil.get(RedisConstant.REDIS_UMS_PREFIX);
        if (correctValidateCode == null) {
            throw new BizException("验证码已过期");
        } else if (!validateCode.equals(correctValidateCode)) {
            throw new BizException("验证码不正确");
        } else {
            redisUtil.del(RedisConstant.REDIS_UMS_PREFIX);
        }

        String username = parameters.get("username");
        String password = parameters.get("password");

        // 移除后续无用参数
        parameters.remove("password");
        parameters.remove("validateCode");
        parameters.remove("uuid");

        // 和密码模式一样的逻辑
        Authentication userAuth = new UsernamePasswordAuthenticationToken(username, password);
        ((AbstractAuthenticationToken) userAuth).setDetails(parameters);

        try {
            userAuth = this.authenticationManager.authenticate(userAuth);
        } catch (AccountStatusException var8) {
            throw new InvalidGrantException(var8.getMessage());
        } catch (BadCredentialsException var9) {
            throw new InvalidGrantException(var9.getMessage());
        }

        if (userAuth != null && userAuth.isAuthenticated()) {
            OAuth2Request storedOAuth2Request = this.getRequestFactory().createOAuth2Request(client, tokenRequest);
            return new OAuth2Authentication(storedOAuth2Request, userAuth);
        } else {
            throw new InvalidGrantException("Could not authenticate user: " + username);
        }
    }
}

