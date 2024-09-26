package com.thinker.cloud.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.Collections;
import java.util.Set;

/**
 * 安全配置
 *
 * @author admin
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = "thinker.security")
public class ThinkerSecurityProperties {

    /**
     * oauth2配置
     */
    private Oauth2Properties oauth2 = new Oauth2Properties();

    /**
     * oauth2配置
     */
    @Data
    public static class Oauth2Properties {

        /**
         * inner安全检查
         */
        private Boolean innerCheck = false;

        /**
         * 白名单接口
         */
        private Set<String> ignoreUrls = Collections.emptySet();
    }

}
