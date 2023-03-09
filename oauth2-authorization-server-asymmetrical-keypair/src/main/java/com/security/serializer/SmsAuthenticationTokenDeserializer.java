package com.security.serializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.granter.SmsAuthenticationToken;
import com.security.model.SecurityUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SmsAuthenticationTokenDeserializer extends JsonDeserializer<SmsAuthenticationToken> {
    @Override
    public SmsAuthenticationToken deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        SmsAuthenticationToken token;
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode jsonNode = mapper.readTree(jsonParser);

        JsonNode detailsNode = jsonNode.get("details");
        JsonNode principal = jsonNode.get("principal");

        // 反序列化UserDetails的实现类
        SecurityUser securityUser = null;
        if (principal != null && !principal.isMissingNode()) {
            securityUser = mapper.readValue(principal.traverse(mapper),
                    SecurityUser.class);
        }

        token = new SmsAuthenticationToken(securityUser, securityUser.getAuthorities());

        // 因为OAuth2AuthenticationDetails无法反序列化，所以使用map进行反序列化
        if (detailsNode != null && !detailsNode.isMissingNode()) {
            Map<String, Object> map = new HashMap<>();
            map.put("remoteAddress", detailsNode.get("remoteAddress").asText());
            map.put("sessionId", detailsNode.get("sessionId").asText(null));

            token.setDetails(map);
        }

        return token;
    }
}
