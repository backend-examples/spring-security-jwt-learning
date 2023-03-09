package com.security.exception;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.util.Map;

/**
 * @Description: 序列化异常类
 * @Package: com.security.exception.GlobalOAuth2ExceptionSerializer
 */
public class GlobalOAuth2ExceptionSerializer extends StdSerializer<GlobalOAuth2Exception> {

    protected GlobalOAuth2ExceptionSerializer() {
        super(GlobalOAuth2Exception.class);
    }

    @Override
    public void serialize(GlobalOAuth2Exception e, JsonGenerator generator, SerializerProvider serializerProvider) throws IOException {
        generator.writeStartObject();
        generator.writeObjectField("status", e.getHttpErrorCode());

        String message = e.getMessage();
        if (message != null) {
            message = HtmlUtils.htmlEscape(message);
        }

        generator.writeStringField("message", message);
        if (e.getAdditionalInformation()!=null) {
            for (Map.Entry<String, String> entry : e.getAdditionalInformation().entrySet()) {
                String key = entry.getKey();
                String add = entry.getValue();
                generator.writeStringField(key, add);
            }
        }

        generator.writeEndObject();
    }
}

