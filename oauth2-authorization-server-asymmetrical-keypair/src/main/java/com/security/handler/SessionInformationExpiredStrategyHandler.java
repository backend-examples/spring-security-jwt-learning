package com.security.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class SessionInformationExpiredStrategyHandler implements SessionInformationExpiredStrategy {

    @Autowired
    FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
        HttpServletResponse response = event.getResponse();
        HttpServletRequest request = event.getRequest();

        String requestUrl = request.getRequestURI();
        Enumeration<String> parameterNames = request.getParameterNames();
        Map<String, Object> parameterMap = new HashMap<>();

        while (parameterNames.hasMoreElements()) {
            String paramName = (String) parameterNames.nextElement();

            String[] paramValues = request.getParameterValues(paramName);
            if (paramValues.length >0) {
                String paramValue = paramValues[0];
                if (paramValue.length() != 0) {
                    parameterMap.put(paramName, paramValue);
                }
            }
        }

        String redirectUrl = requestUrl + "?";

        for (Map.Entry<String, Object> param:parameterMap.entrySet()) {
            System.out.println("Key = " + param.getKey() + ", Value = " + param.getValue());
            redirectUrl += param.getKey() + "=" + param.getValue() + "&";
        }

        log.info("requestUrl 过期：" + requestUrl);
        log.info("过期参数：" + parameterMap);
        log.info("重定向地址：" +redirectUrl);

        response.sendRedirect(request.getContextPath() + "/auth/invalidSession?redirectUrl=" + URLEncoder.encode(redirectUrl));
    }
}
