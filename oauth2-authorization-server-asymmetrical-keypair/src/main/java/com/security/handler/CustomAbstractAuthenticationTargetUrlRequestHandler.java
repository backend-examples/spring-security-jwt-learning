package com.security.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.security.utils.ResponseResult;
import com.security.utils.ResponseUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public abstract class CustomAbstractAuthenticationTargetUrlRequestHandler {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private String targetUrlParameter = null;
    private String defaultTargetUrl = "/";
    private boolean alwaysUseDefaultTargetUrl = false;
    private boolean useReferer = false;
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    protected CustomAbstractAuthenticationTargetUrlRequestHandler() {
    }

    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = this.determineTargetUrl(request, response, authentication);
        if (response.isCommitted()) {
            this.logger.debug(LogMessage.format("Did not redirect to %s since response already committed.", targetUrl));
        } else {
//            this.redirectStrategy.sendRedirect(request, response, targetUrl);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("targetUrl", targetUrl);
            ResponseUtils.result(response, ResponseResult.builder().ok(resultMap).build());
        }
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        return this.determineTargetUrl(request, response);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        if (this.isAlwaysUseDefaultTargetUrl()) {
            return this.defaultTargetUrl;
        } else {
            String targetUrl = null;
            if (this.targetUrlParameter != null) {
                targetUrl = request.getParameter(this.targetUrlParameter);
                if (StringUtils.hasText(targetUrl)) {
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace(LogMessage.format("Using url %s from request parameter %s", targetUrl, this.targetUrlParameter));
                    }

                    return targetUrl;
                }
            }

            if (this.useReferer && !StringUtils.hasLength(targetUrl)) {
                targetUrl = request.getHeader("Referer");
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace(LogMessage.format("Using url %s from Referer header", targetUrl));
                }
            }

            if (!StringUtils.hasText(targetUrl)) {
                targetUrl = this.defaultTargetUrl;
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace(LogMessage.format("Using default url %s", targetUrl));
                }
            }

            return targetUrl;
        }
    }

    protected final String getDefaultTargetUrl() {
        return this.defaultTargetUrl;
    }

    public void setDefaultTargetUrl(String defaultTargetUrl) {
        Assert.isTrue(UrlUtils.isValidRedirectUrl(defaultTargetUrl), "defaultTarget must start with '/' or with 'http(s)'");
        this.defaultTargetUrl = defaultTargetUrl;
    }

    public void setAlwaysUseDefaultTargetUrl(boolean alwaysUseDefaultTargetUrl) {
        this.alwaysUseDefaultTargetUrl = alwaysUseDefaultTargetUrl;
    }

    protected boolean isAlwaysUseDefaultTargetUrl() {
        return this.alwaysUseDefaultTargetUrl;
    }

    public void setTargetUrlParameter(String targetUrlParameter) {
        if (targetUrlParameter != null) {
            Assert.hasText(targetUrlParameter, "targetUrlParameter cannot be empty");
        }

        this.targetUrlParameter = targetUrlParameter;
    }

    protected String getTargetUrlParameter() {
        return this.targetUrlParameter;
    }

    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    protected RedirectStrategy getRedirectStrategy() {
        return this.redirectStrategy;
    }

    public void setUseReferer(boolean useReferer) {
        this.useReferer = useReferer;
    }
}

