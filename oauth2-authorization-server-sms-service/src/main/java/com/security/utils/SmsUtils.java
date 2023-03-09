package com.security.utils;


import com.alibaba.fastjson2.JSON;
import com.security.properties.SmsProperties;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 短信服务
 */
@Component
public class SmsUtils implements DisposableBean {

    @Autowired
    private SmsProperties smsProperties;

    private Credential credential;

    private HttpProfile httpProfile = new HttpProfile();

    private ClientProfile clientProfile = new ClientProfile();

    private SmsClient client;

    private SendSmsRequest request = new SendSmsRequest();

    private SendSmsResponse sendSmsResponse;

    public SmsUtils builder() {
        this.setCredential();
        this.setHttpProfile();
        this.setClientProfile();
        this.setClient();
        this.setRequest();

        return this;
    }

    public void setCredential() {
        this.credential = new Credential(smsProperties.getSecretId(), smsProperties.getSecretKey());
    }

    public void setHttpProfile() {
        this.httpProfile.setReqMethod(smsProperties.getRequestMethod());
        this.httpProfile.setConnTimeout(smsProperties.getConnTimeout());
        this.httpProfile.setEndpoint(smsProperties.getEndpoint());
    }

    public void setClientProfile() {
        this.clientProfile.setSignMethod(smsProperties.getSignMethod());
        this.clientProfile.setHttpProfile(httpProfile);
    }

    public void setClient() {
        this.client = new SmsClient(this.credential, smsProperties.getRegion(), this.clientProfile);
    }

    public void setRequest() {
        this.request.setSmsSdkAppId(smsProperties.getSdkAppId());
        this.request.setSignName(smsProperties.getSignName());
        this.request.setTemplateId(smsProperties.getTemplateId());
    }

    public void setSendSmsResponse(SendSmsResponse sendSmsResponse) {
        this.sendSmsResponse = sendSmsResponse;
    }

    public SendSmsResponse getSendSmsResponse() {
        return this.sendSmsResponse;
    }

    public SmsUtils sendSmsCode(String phone, String smsCode) throws TencentCloudSDKException {
        String[] templateParamSet = Arrays.asList(smsCode, smsProperties.getTime()).toArray(new String[Arrays.asList(smsCode, smsProperties.getTime()).size()]);
        String[] phoneNumberSet = Arrays.asList(phone).toArray(new String[Arrays.asList(phone).size()]);

        this.request.setPhoneNumberSet(phoneNumberSet);
        this.request.setTemplateParamSet(templateParamSet);
        this.setSendSmsResponse(this.client.SendSms(this.request));

        return this;
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("SmsUtils 销毁中。。。");
    }

    public String toString() {
        return SendSmsResponse.toJsonString(this.sendSmsResponse);
    }
}
