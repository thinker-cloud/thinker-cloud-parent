package com.thinker.cloud.security.credential.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 登录凭证验证
 *
 * @author admin
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class CredentialValid implements Serializable {

    @Serial
    private static final long serialVersionUID = -8848426551856152538L;

    /**
     * 凭证主体
     */
    @NotBlank(message = "主体不能为空")
    private String subject;

    /**
     * 凭证
     */
    @NotBlank(message = "凭证不能为空")
    private String credential;

    /**
     * 是否忽略大小写
     */
    private Boolean ignoreCase = Boolean.TRUE;

    /**
     * 验证后是否删除
     */
    private Boolean isDelete = Boolean.TRUE;
}
