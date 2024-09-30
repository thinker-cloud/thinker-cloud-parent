package com.thinker.cloud.security.credential.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;

/**
 * 登录凭证参数
 *
 * @author admin
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class CredentialParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 3728609210506412850L;

    /**
     * 凭证长度
     */
    @NotNull(message = "凭证长度不能为空")
    @Min(value = 4, message = "凭证长度最小长度不能小于4位")
    @Max(value = 50, message = "凭证长度最大长度不能大于50位")
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
    @Max(value = 1000 * 60 * 60, message = "凭证最大时间薇1小时")
    private Long expire = Duration.ofMinutes(3L).toMillis();

}
