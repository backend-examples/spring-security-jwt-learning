package com.security.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());


    public CustomSuccessHandler() {
        super();
//        super.setDefaultTargetUrl("/toLogin");
        this.setDefaultTargetUrl("/toLogin");
//        super.setTargetUrlParameter("/toLogin");
    }
}
