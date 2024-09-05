package com.thinker.cloud.security.utils;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;

/**
 * 建议所有异常都使用此工具类型 避免无法复写 SpringSecurityMessageSource
 *
 * @author admin
 * @see org.springframework.security.core.SpringSecurityMessageSource 框架自身异常处理
 */
public class SecurityMessageSourceUtil extends ReloadableResourceBundleMessageSource {

    // ~ Constructors
    // ===================================================================================================

    public SecurityMessageSourceUtil() {
        setBasename("classpath:messages/messages");
        setDefaultLocale(Locale.CHINA);
    }

    // ~ Methods
    // ========================================================================================================

    public static MessageSourceAccessor getAccessor() {
        return new MessageSourceAccessor(new SecurityMessageSourceUtil());
    }

}
