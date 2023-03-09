package com.security.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@PropertySource("classpath:application.yml")
public class SecretProperties {

    @Value("${secret.password}")
    private String password;

    @Value("${secret.alias}")
    private String alias;

    @Value("${secret.file}")
    private String file;
}
