package com.security.utils;

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
public class SmsUtils implements DisposableBean {

    private SendSmsResponse sendSmsResponse;

    public static Builder builder() {
        return new Builder();
    }

    public SmsUtils(Builder builder) {
        this.sendSmsResponse = builder.sendSmsResponse;
    }

    public void setSendSmsResponse(SendSmsResponse sendSmsResponse) {
        this.sendSmsResponse = sendSmsResponse;
    }

    public SendSmsResponse getSendSmsResponse() {
        return this.sendSmsResponse;
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("SmsUtils 销毁中。。。");
    }

    public static final class Builder {

        @Autowired
        private SmsProperties smsProperties;

        private Credential credential;

        private HttpProfile httpProfile = new HttpProfile();

        private ClientProfile clientProfile = new ClientProfile();

        private SmsClient client;

        private SendSmsRequest request = new SendSmsRequest();

        private SendSmsResponse sendSmsResponse;

        public Builder() {
            this.setCredential();
            this.setHttpProfile();
            this.setClientProfile();
            this.setClient();
            this.setRequest();
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

        public Builder sendSmsCode(String phone, String smsCode) throws TencentCloudSDKException {
            String[] templateParamSet = Arrays.asList(smsCode, smsProperties.getTime()).toArray(new String[Arrays.asList(smsCode, smsProperties.getTime()).size()]);
            String[] phoneNumberSet = Arrays.asList(phone).toArray(new String[Arrays.asList(phone).size()]);

            this.request.setPhoneNumberSet(phoneNumberSet);
            this.request.setTemplateParamSet(templateParamSet);
            this.setSendSmsResponse(this.client.SendSms(this.request));

            return this;
        }

        public SmsUtils build() {
            return new SmsUtils(this);
        }
    }

    public String toString() {
        return SendSmsResponse.toJsonString(this.sendSmsResponse);
    }
}
