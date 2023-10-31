package com.thinker.cloud.security.credential;


import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.thinker.cloud.core.exception.FailException;
import com.thinker.cloud.security.credential.model.CredentialParam;
import com.thinker.cloud.security.credential.model.CredentialType;
import com.thinker.cloud.security.credential.model.CredentialValid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


/**
 * 凭据模板
 *
 * @author admin
 */
@Slf4j
@Component
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
     * @return String
     */
    public String generate(CredentialParam param) {
        Object codeObj = redisTemplate.opsForValue().get(CREDENTIAL_CODE_KEY + param.getSubject());
        if (codeObj != null) {
            log.info("验证码未过期:{}，{}", param.getSubject(), codeObj);
            throw new FailException("验证码发送过频繁,请稍后再试");
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
        redisTemplate.opsForValue().set(CREDENTIAL_CODE_KEY + param.getSubject(), credential,
                expire, TimeUnit.MILLISECONDS);

        return credential;
    }


    /**
     * 验证码验证
     *
     * @param validation validation
     * @return Boolean
     */
    public Boolean validate(CredentialValid validation) {
        String key = CREDENTIAL_CODE_KEY + validation.getSubject();
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        if (!Optional.ofNullable(redisTemplate.hasKey(key)).orElse(false)) {
            return Boolean.FALSE;
        }

        Object codeObj = redisTemplate.opsForValue().get(key);
        if (codeObj == null) {
            return Boolean.FALSE;
        }

        String saveCode = codeObj.toString();
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
