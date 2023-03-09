package com.security.handler;

import com.security.enums.CodeEnum;
import com.security.properties.RedisProperties;
import com.security.redis.SessionRepository;
import com.security.utils.CookiesUtils;
import com.security.utils.ResponseResult;
import com.security.utils.ResponseUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.log.LogMessage;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.RedirectUrlBuilder;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.session.Session;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 重写 LoginUrlAuthenticationEntryPoint
 * 返回自定义数据
 */
public class CustomLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {
    private static final Log logger = LogFactory.getLog(LoginUrlAuthenticationEntryPoint.class);
    private PortMapper portMapper = new PortMapperImpl();
    private PortResolver portResolver = new PortResolverImpl();
    private String loginFormUrl;
    private boolean forceHttps = false;
    private boolean useForward = false;
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private final String XMLHttpRequest = "XMLHttpRequest";

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private RedisProperties redisProperties;

    public CustomLoginUrlAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
        Assert.notNull(loginFormUrl, "loginFormUrl cannot be null");
        this.loginFormUrl = loginFormUrl;
    }

    public void afterPropertiesSet() {
        Assert.isTrue(StringUtils.hasText(this.loginFormUrl) && UrlUtils.isValidRedirectUrl(this.loginFormUrl), "loginFormUrl must be specified and must be a valid redirect URL");
        Assert.isTrue(!this.useForward || !UrlUtils.isAbsoluteUrl(this.loginFormUrl), "useForward must be false if using an absolute loginFormURL");
        Assert.notNull(this.portMapper, "portMapper must be specified");
        Assert.notNull(this.portResolver, "portResolver must be specified");
    }

    protected String determineUrlToUseForThisRequest(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        return this.getLoginFormUrl();
    }

    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String redirectUrl;
        String requestedWith = request.getHeader("x-requested-with");

        // 单点登录请求校验的返回数据
        if (XMLHttpRequest.equals(requestedWith)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            Map<String, Cookie> cookies = CookiesUtils.getCookies(request);
            Cookie cookie = cookies.get(redisProperties.getCookieName());
            if (cookie != null) {
                Session session = sessionRepository.getSessionById(cookie.getValue());

                // 单点登录，当redis中已存在对应客户端session时，删掉
                // 解决某一客户端未授权，导致其它客户端无法进行授权登录
                if (session != null) {
                    sessionRepository.deleteSessionById(session.getId());
                }
            }

            redirectUrl = this.buildRedirectUrlToLoginPage(request, response, authException);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("location", redirectUrl);
            ResponseUtils.result(response, ResponseResult.builder().error(CodeEnum.NOT_AUTHENTICATION.getCode(), CodeEnum.NOT_AUTHENTICATION.getMessage()).data(resultMap));
        }

        // 默认的登录校验返回数据
        if (!XMLHttpRequest.equals(requestedWith)) {
            if (!this.useForward) {
                redirectUrl = this.buildRedirectUrlToLoginPage(request, response, authException);
                this.redirectStrategy.sendRedirect(request, response, redirectUrl);
            } else {
                redirectUrl = null;
                if (this.forceHttps && "http".equals(request.getScheme())) {
                    redirectUrl = this.buildHttpsRedirectUrlForRequest(request);
                }

                if (redirectUrl != null) {
                    this.redirectStrategy.sendRedirect(request, response, redirectUrl);
                } else {
                    String loginForm = this.determineUrlToUseForThisRequest(request, response, authException);
                    logger.debug(LogMessage.format("Server side forward to: %s", loginForm));
                    RequestDispatcher dispatcher = request.getRequestDispatcher(loginForm);
                    dispatcher.forward(request, response);
                }
            }
        }
    }

    protected String buildRedirectUrlToLoginPage(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        String loginForm = this.determineUrlToUseForThisRequest(request, response, authException);
        if (UrlUtils.isAbsoluteUrl(loginForm)) {
            return loginForm;
        } else {
            int serverPort = this.portResolver.getServerPort(request);
            String scheme = request.getScheme();
            RedirectUrlBuilder urlBuilder = new RedirectUrlBuilder();
            urlBuilder.setScheme(scheme);
            urlBuilder.setServerName(request.getServerName());
            urlBuilder.setPort(serverPort);
            urlBuilder.setContextPath(request.getContextPath());
            urlBuilder.setPathInfo(loginForm);
            if (this.forceHttps && "http".equals(scheme)) {
                Integer httpsPort = this.portMapper.lookupHttpsPort(serverPort);
                if (httpsPort != null) {
                    urlBuilder.setScheme("https");
                    urlBuilder.setPort(httpsPort);
                } else {
                    logger.warn(LogMessage.format("Unable to redirect to HTTPS as no port mapping found for HTTP port %s", serverPort));
                }
            }

            return urlBuilder.getUrl();
        }
    }

    protected String buildHttpsRedirectUrlForRequest(HttpServletRequest request) throws IOException, ServletException {
        int serverPort = this.portResolver.getServerPort(request);
        Integer httpsPort = this.portMapper.lookupHttpsPort(serverPort);
        if (httpsPort != null) {
            RedirectUrlBuilder urlBuilder = new RedirectUrlBuilder();
            urlBuilder.setScheme("https");
            urlBuilder.setServerName(request.getServerName());
            urlBuilder.setPort(httpsPort);
            urlBuilder.setContextPath(request.getContextPath());
            urlBuilder.setServletPath(request.getServletPath());
            urlBuilder.setPathInfo(request.getPathInfo());
            urlBuilder.setQuery(request.getQueryString());
            return urlBuilder.getUrl();
        } else {
            logger.warn(LogMessage.format("Unable to redirect to HTTPS as no port mapping found for HTTP port %s", serverPort));
            return null;
        }
    }

    public void setForceHttps(boolean forceHttps) {
        this.forceHttps = forceHttps;
    }

    protected boolean isForceHttps() {
        return this.forceHttps;
    }

    public String getLoginFormUrl() {
        return this.loginFormUrl;
    }

    public void setPortMapper(PortMapper portMapper) {
        Assert.notNull(portMapper, "portMapper cannot be null");
        this.portMapper = portMapper;
    }

    protected PortMapper getPortMapper() {
        return this.portMapper;
    }

    public void setPortResolver(PortResolver portResolver) {
        Assert.notNull(portResolver, "portResolver cannot be null");
        this.portResolver = portResolver;
    }

    protected PortResolver getPortResolver() {
        return this.portResolver;
    }

    public void setUseForward(boolean useForward) {
        this.useForward = useForward;
    }

    protected boolean isUseForward() {
        return this.useForward;
    }
}
