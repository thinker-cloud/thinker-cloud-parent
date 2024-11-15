package com.thinker.cloud.security.repository.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基于redis存储的客户端实体
 *
 * @author vains
 */
@Data
@RedisHash(value = "oauth:client")
public class RedisRegisteredClient implements Serializable {

    @Serial
    private static final long serialVersionUID = 1716825735530905215L;

    /**
     * 主键
     */
    @Id
    private String id;

    /**
     * 客户端名称
     */
    private String name;

    /**
     * 客户端id
     */
    @Indexed
    private String clientId;

    /**
     * 客户端key
     */
    private String clientKey;

    /**
     * 客户端秘钥
     */
    private String clientSecret;

    /**
     * 是否启用 0.禁用 1.启用
     */
    private Boolean enabled;

    /**
     * 授权范围
     */
    private String scopes;

    /**
     * 认证方式
     */
    private String methods;

    /**
     * 授权类型
     */
    private String grantTypes;

    /**
     * 回调跳转URL
     */
    private String redirectUris;

    /**
     * 登出回调地址
     */
    private String logoutRedirectUris;

    /**
     * 客户端秘钥过期时间
     */
    private LocalDateTime expiresAt;

    /**
     * 访问令牌有效期（秒）
     */
    private Integer accessTokenValidity;

    /**
     * 刷新令牌有效期（秒）
     */
    private Integer refreshTokenValidity;

    /**
     * 前端密码加密
     */
    private Boolean isEncode;

    /**
     * 验证码开关
     */
    private Boolean isCaptcha;

    /**
     * IP白名单
     */
    private String ipWhiteList;

    /**
     * 附加信息
     */
    private String additionalInfo;

    /**
     * 自动授权
     */
    private String autoApprove;

    /**
     * 备注描述
     */
    private String description;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 创建人id
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改人id
     */
    private Long updateBy;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
}
