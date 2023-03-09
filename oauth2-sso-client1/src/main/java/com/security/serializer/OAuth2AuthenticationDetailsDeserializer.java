package com.security.serializer;

import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

public class OAuth2AuthenticationDetailsDeserializer extends JsonDeserializer<OAuth2AuthenticationDetails> {

    @Override
    public OAuth2AuthenticationDetails deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode jsonNode = mapper.readTree(jsonParser);
//        JsonNode remoteAddress = jsonNode.get("remoteAddress");
//        JsonNode sessionId = jsonNode.get("sessionId");
//        JsonNode tokenValue = jsonNode.get("tokenValue");
//        JsonNode tokenType = jsonNode.get("tokenType");
//        JsonNode display = jsonNode.get("display");
//        JsonNode decodedDetails = jsonNode.get("decodedDetails");

//        jsonNode.traverse();
        Map<String, Object> map = (Map<String, Object>) mapper.readValues(jsonNode.traverse(mapper), Map.class);
//        FormatSchema parserSchema = objectMappingIterator.getParserSchema();


//        HttpServletRequest request =
//                ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
//
//        request.setAttribute(OAuth2AuthenticationDetails.class.getSimpleName() + ".ACCESS_TOKEN_VALUE", tokenValue);
//        request.setAttribute(OAuth2AuthenticationDetails.class.getSimpleName() + ".ACCESS_TOKEN_TYPE", tokenType);
        OAuth2AuthenticationDetails oAuth2AuthenticationDetails;

        return null;
    }
}
