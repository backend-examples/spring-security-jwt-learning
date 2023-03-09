package com.security.validate;

import com.security.exception.ValidateCodeException;
import com.security.redis.ValidateCodeRepository;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * 模板方法实现抽象策略
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/7/28 下午9:48
 */
public abstract class AbstractValidateCodeProcessor implements ValidateCodeProcessor {

    /**
     * 收集系统中所有的 {@link ValidateCodeGenerator} 接口实现。
     */
    @Autowired
    private Map<String, ValidateCodeGenerator> validateCodeGenerators;

    @Autowired
    private ValidateCodeRepository validateCodeRepository;

    @Override
    public void create(ServletWebRequest request) throws Exception {
        String validateCode = generate(request);
        String phoneOrEmail = request.getParameter("phoneOrEmail");
        save(request, phoneOrEmail, validateCode);
        send(request, phoneOrEmail, validateCode);
    }

    @Override
    public void validate(ServletWebRequest request) {
        String type = getValidateCodeType(request);
        String requestCode = request.getParameter("code");
        String phoneOrEmail = request.getParameter("phoneOrEmail");
        String code = validateCodeRepository.get(phoneOrEmail, type);
        // 验证码是否存在
        if (Objects.isNull(code)) {
            throw new ValidateCodeException("获取验证码失败，请检查输入是否正确或重新发送！");
        }
        // 验证码输入是否正确
        if (!code.equalsIgnoreCase(requestCode)) {
            throw new ValidateCodeException("验证码不正确，请重新输入！");
        }
        // 验证通过后，清除验证码
        validateCodeRepository.remove(phoneOrEmail, type);
    }

    /**
     * 发送验证码，由子类实现
     *
     * @param request      请求
     * @param validateCode 验证码
     */
    protected abstract void send(ServletWebRequest request, String phoneOrEmail, String validateCode) throws TencentCloudSDKException, IOException;

    /**
     * 保存验证码，保存到 redis 中
     *
     * @param request      请求
     * @param validateCode 验证码
     */
    private void save(ServletWebRequest request, String phoneOrEmail, String validateCode) {
        validateCodeRepository.save(phoneOrEmail, validateCode, getValidateCodeType(request));
    }

    /**
     * 生成验证码
     *
     * @param request 请求
     * @return 验证码
     */
    private String generate(ServletWebRequest request) {
        String type = getValidateCodeType(request);
        String componentName = type + ValidateCodeGenerator.class.getSimpleName();
        ValidateCodeGenerator generator = validateCodeGenerators.get(componentName);
        if (Objects.isNull(generator)) {
            throw new ValidateCodeException("验证码生成器 " + componentName + " 不存在。");
        }

        return generator.generate(request);
    }

    /**
     * 根据请求 url 获取验证码类型
     *
     * @return 结果
     */
    private String getValidateCodeType(ServletWebRequest request) {
        String uri = request.getRequest().getRequestURI();
        int index = uri.lastIndexOf("/") + 1;
        return uri.substring(index).toLowerCase();
    }

}