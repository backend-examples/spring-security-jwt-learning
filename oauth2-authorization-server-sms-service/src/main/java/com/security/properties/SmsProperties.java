package com.security.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "sms")
public class SmsProperties {

    private String secretId;

    private String secretKey;

    private String requestMethod;

    private int connTimeout;

    private String endpoint;

    private String signMethod;

    private String region;

    private String sdkAppId;

    private String signName;

    private String templateId;

    private String time;
}
