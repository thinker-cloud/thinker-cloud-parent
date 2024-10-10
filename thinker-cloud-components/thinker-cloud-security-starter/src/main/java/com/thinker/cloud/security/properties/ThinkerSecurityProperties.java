package com.thinker.cloud.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 安全配置
 *
 * @author admin
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = "thinker.cloud.security")
public class ThinkerSecurityProperties {

    /**
     * 网关解密登录前端密码 秘钥
     */
    private String encodeKey;

    /**
     * 网关不需要校验验证码的客户端
     */
    private List<String> ignoreClients = Collections.emptyList();

    /**
     * inner安全检查
     */
    private Boolean innerCheck = false;

    /**
     * 白名单接口
     */
    private Set<String> ignoreUrls = Collections.emptySet();

}
