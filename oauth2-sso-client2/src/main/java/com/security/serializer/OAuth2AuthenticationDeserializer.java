package com.security.serializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class OAuth2AuthenticationDeserializer extends JsonDeserializer<OAuth2Authentication> {

    @Override
    public OAuth2Authentication deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        OAuth2Authentication token;
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode jsonNode = mapper.readTree(jsonParser);
        JsonNode storedRequest = jsonNode.get("storedRequest");
        JsonNode userAuthenticationNode = jsonNode.get("userAuthentication");

        ObjectNode object = (ObjectNode)storedRequest;
        // 必须删除refresh
        object.remove("refresh");

        OAuth2Request request = mapper.readValue(object.traverse(mapper), OAuth2Request.class);
        Authentication auth = null;
        if (userAuthenticationNode != null && !userAuthenticationNode.isMissingNode()) {
            auth = mapper.readValue(userAuthenticationNode.traverse(mapper),
                    UsernamePasswordAuthenticationToken.class);
        }
        token = new OAuth2Authentication(request, auth);
        JsonNode detailsNode = jsonNode.get("details");

        // 因为OAuth2AuthenticationDetails无法反序列化，所以使用map进行反序列化
        if (detailsNode != null && !detailsNode.isMissingNode()) {
            Map<String, Object> map = new HashMap<>();
            map.put("remoteAddress", detailsNode.get("remoteAddress").asText());
            map.put("sessionId", detailsNode.get("sessionId").asText());
            map.put("tokenValue", detailsNode.get("tokenValue").asText());
            map.put("tokenType", detailsNode.get("tokenType").asText());
            map.put("decodedDetails", detailsNode.get("decodedDetails"));


//            token.setDetails(mapper.readValue(detailsNode.traverse(mapper), OAuth2AuthenticationDetails.class));
            token.setDetails(map);
        }

        return token;
    }
}
