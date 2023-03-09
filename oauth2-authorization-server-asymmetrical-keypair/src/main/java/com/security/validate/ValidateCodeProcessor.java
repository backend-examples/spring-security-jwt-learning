package com.security.validate;

import org.springframework.web.context.request.ServletWebRequest;

public interface ValidateCodeProcessor {

    /**
     * 创建验证码
     *
     * @param request 请求
     * @throws Exception 异常
     */
    void create(ServletWebRequest request) throws Exception;

    /**
     * 验证验证码
     *
     * @param request 请求
     */
    void validate(ServletWebRequest request);

}
