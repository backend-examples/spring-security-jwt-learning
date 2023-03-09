package com.security.validate;

import com.security.utils.ResponseResult;
import com.security.utils.ResponseUtils;
import com.security.utils.SmsUtils;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SmsValidateCodeProcessor extends AbstractValidateCodeProcessor {

    @Autowired
    private SmsUtils smsUtils;

    @Override
    protected void send(ServletWebRequest request, String phone, String validateCode) throws TencentCloudSDKException, IOException {
        smsUtils.builder().sendSmsCode(phone, validateCode);
        ResponseUtils.result(request.getResponse(), ResponseResult.builder().success(null));
        System.out.println(phone +
                "手机验证码发送成功，验证码为：" + validateCode);
    }
}
