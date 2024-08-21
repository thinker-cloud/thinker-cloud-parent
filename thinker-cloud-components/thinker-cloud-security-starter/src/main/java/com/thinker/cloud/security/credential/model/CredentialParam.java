package com.thinker.cloud.security.credential.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.Duration;

/**
 * 凭证参数
 *
 * @author admin
 */
@Data
public class CredentialParam implements Serializable {

    public CredentialParam(String subject) {
        this.subject = subject;
    }

    /**
     * 凭证大小
     */
    @Min(value = 4, message = "凭证长度最小长度不能小于4位")
    @Min(value = 50, message = "凭证长度最大长度不能大于50位")
    @NotNull(message = "凭证长度不能为空")
    private Integer size = 6;

    /**
     * 凭证类型
     */
    @NotNull(message = "凭证类型不能为空")
    private CredentialType type = CredentialType.NUMBER;

    /**
     * 主体
     */
    @NotBlank(message = "主体不能为空")
    private String subject;

    /**
     * 过期时间
     */
    @Min(value = 1000 * 15, message = "凭证最少失效时间为15秒")
    @Max(value = 1000 * 60 * 60, message = "凭证最大时间为1小时")
    private Long expire = Duration.ofMinutes(3L).toMillis();

}
