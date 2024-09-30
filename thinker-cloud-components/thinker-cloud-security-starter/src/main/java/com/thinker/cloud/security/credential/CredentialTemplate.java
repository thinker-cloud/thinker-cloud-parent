package com.thinker.cloud.security.credential;


import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.thinker.cloud.core.exception.ValidationException;
import com.thinker.cloud.security.credential.model.CredentialParam;
import com.thinker.cloud.security.credential.model.CredentialType;
import com.thinker.cloud.security.credential.model.CredentialValid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


/**
 * 凭证模板生成器
 *
 * @author admin
 */
@Slf4j
@AllArgsConstructor
public class CredentialTemplate {
    /**
     * 验证码缓存名称
     */
    private static final String CREDENTIAL_CODE_KEY = "CREDENTIAL_CODE_KEY:";

    private final StringRedisTemplate redisTemplate;


    /**
     * 生成验证码
     *
     * @param param param
     */
    public String generate(CredentialParam param) {
        Object codeObj = redisTemplate.opsForValue().get(CREDENTIAL_CODE_KEY + param.getSubject());
        if (codeObj != null) {
            log.info("验证码未过期:{}，{}", param.getSubject(), codeObj);
            throw new ValidationException("验证码发送过频繁,请稍后再试");
        }

        String credential;
        int size = Optional.ofNullable(param.getSize()).orElse(6);
        CredentialType type = Optional.ofNullable(param.getType()).orElse(CredentialType.NUMBER);
        long expire = Optional.ofNullable(param.getExpire()).orElse(Duration.ofMinutes(3L).toMillis());
        if (CredentialType.NUMBER.equals(type)) {
            credential = RandomUtil.randomNumbers(size);
        } else {
            credential = RandomUtil.randomString(size);
        }

        log.debug("生成验证码成功:{},{}", param.getSubject(), credential);
        redisTemplate.opsForValue().set(CREDENTIAL_CODE_KEY + param.getSubject(), credential, expire, TimeUnit.MILLISECONDS);
        return credential;
    }

    public Boolean validate(CredentialValid validation) {
        String key = CREDENTIAL_CODE_KEY + validation.getSubject();

        if (!Optional.ofNullable(redisTemplate.hasKey(key)).orElse(false)) {
            return Boolean.FALSE;
        }

        String saveCode = redisTemplate.opsForValue().get(key);
        if (Objects.isNull(saveCode)) {
            return Boolean.FALSE;
        }

        if (StrUtil.isBlank(saveCode)) {
            redisTemplate.delete(key);
            return Boolean.FALSE;
        }

        if (!StrUtil.equals(saveCode, validation.getCredential())) {
            redisTemplate.delete(key);
            return Boolean.FALSE;
        }

        return redisTemplate.delete(key);
    }

}
