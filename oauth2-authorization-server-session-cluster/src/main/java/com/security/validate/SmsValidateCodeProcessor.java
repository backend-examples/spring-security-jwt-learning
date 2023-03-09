package com.security.validate;

import com.security.utils.ResponseResult;
import com.security.utils.ResponseUtils;
import com.security.utils.SmsUtils;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SmsValidateCodeProcessor extends AbstractValidateCodeProcessor {

    @Override
    protected void send(ServletWebRequest request, String phone, String validateCode) throws TencentCloudSDKException, IOException {
        SmsUtils.builder().sendSmsCode(phone, validateCode);
        ResponseUtils.result(request.getResponse(), ResponseResult.builder().ok("手机验证码发送成功").build());
        System.out.println(phone +
                "手机验证码发送成功，验证码为：" + validateCode);
    }
}
