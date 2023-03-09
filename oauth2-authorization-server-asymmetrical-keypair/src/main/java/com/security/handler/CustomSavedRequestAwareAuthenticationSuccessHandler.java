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
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CustomSavedRequestAwareAuthenticationSuccessHandler extends CustomSimpleUrlAuthenticationSuccessHandler {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private RequestCache requestCache = new HttpSessionRequestCache();

    public CustomSavedRequestAwareAuthenticationSuccessHandler() {
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        SavedRequest savedRequest = this.requestCache.getRequest(request, response);
        if (savedRequest == null) {
            super.onAuthenticationSuccess(request, response, authentication);
        } else {
            String targetUrlParameter = this.getTargetUrlParameter();
            if (!this.isAlwaysUseDefaultTargetUrl() && (targetUrlParameter == null || !StringUtils.hasText(request.getParameter(targetUrlParameter)))) {
                this.clearAuthenticationAttributes(request);
                String targetUrl = savedRequest.getRedirectUrl();
//                this.getRedirectStrategy().sendRedirect(request, response, targetUrl);
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("targetUrl", targetUrl);

                ResponseUtils.result(response, ResponseResult.builder().ok(resultMap).build());
            } else {
                this.requestCache.removeRequest(request, response);
                super.onAuthenticationSuccess(request, response, authentication);
            }
        }
    }

    public void setRequestCache(RequestCache requestCache) {
        this.requestCache = requestCache;
    }
}

