package com.thinker.cloud.security.serializer;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.thinker.cloud.security.model.AuthUser;
import com.thinker.cloud.security.token.PasswordAuthenticationToken;
import com.thinker.cloud.security.token.SmsAuthenticationToken;
import org.springframework.security.jackson2.SecurityJackson2Modules;

/**
 * Jackson自定义授权序列化
 *
 * @author admin
 **/
public class CustomOAuth2AuthorizationJackson2Module extends SimpleModule {

    public CustomOAuth2AuthorizationJackson2Module() {
        super(CustomOAuth2AuthorizationJackson2Module.class.getName(), new Version(1, 0, 0, null, null, null));
    }

    @Override
    public void setupModule(SetupContext context) {
        SecurityJackson2Modules.enableDefaultTyping(context.getOwner());
        context.setMixInAnnotations(AuthUser.class, AuthUserMixin.class);
        context.setMixInAnnotations(PasswordAuthenticationToken.class, PasswordAuthenticationTokenMixin.class);
        context.setMixInAnnotations(SmsAuthenticationToken.class, SmsAuthenticationTokenMixin.class);
    }
}
