package com.thinker.cloud.security.credential.model;

import java.io.Serializable;

/**
 * 凭证类型
 *
 * @author admin
 */

public enum CredentialType implements Serializable {

    /**
     * 全数字验证码
     */
    NUMBER,

    /**
     * 数字英文随机验证码
     */
    NORMAL,
    ;
}
