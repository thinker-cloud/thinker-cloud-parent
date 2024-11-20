package com.thinker.cloud.security.token.jackson2;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.thinker.cloud.security.model.AuthUser;
import com.thinker.cloud.security.token.SmsAuthenticationToken;

import java.io.IOException;

/**
 * SMS短信登录授权认证 反序列化处理
 *
 * @author admin
 */
public class SmsAuthenticationTokenDeserializer extends StdDeserializer<SmsAuthenticationToken> {

    private static final TypeReference<Object> OBJECT = new TypeReference<>() {
    };

    protected SmsAuthenticationTokenDeserializer() {
        super(SmsAuthenticationToken.class);
    }

    @Override
    public SmsAuthenticationToken deserialize(JsonParser jp, DeserializationContext context) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode jsonNode = mapper.readTree(jp);

        AuthUser principal = mapper.convertValue(this.readJsonNode(jsonNode, "principal"), AuthUser.class);
        JsonNode detailsNode = this.readJsonNode(jsonNode, "details");

        SmsAuthenticationToken authenticationToken = new SmsAuthenticationToken(principal);
        if (!detailsNode.isNull() && !detailsNode.isMissingNode()) {
            Object details = mapper.readValue(detailsNode.toString(), OBJECT);
            authenticationToken.setDetails(details);
        }
        return authenticationToken;
    }

    private JsonNode readJsonNode(JsonNode jsonNode, String field) {
        return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
    }
}
