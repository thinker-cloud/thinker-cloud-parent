package com.thinker.cloud.security.credential.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 凭证有效
 *
 * @author admin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CredentialValid implements Serializable {

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
