package com.thinker.cloud.session.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thinker.cloud.redis.RedisConfiguration;
import com.thinker.cloud.session.properties.SessionProperties;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.jackson2.CoreJackson2Module;
import org.springframework.security.web.jackson2.WebJackson2Module;
import org.springframework.security.web.jackson2.WebServletJackson2Module;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * Cookie序列化 与 Session共享 配置
 *
 * @author admin
 */
@Slf4j
@AllArgsConstructor
public class SessionSerializerConfiguration {

    private final SessionProperties sessionProperties;

    /**
     * 在主域中储存Cookie，子域中共享Cookie
     */
    @Bean
    public CookieSerializer cookieSerializer() {
        // 默认 Cookie 序列化
        DefaultCookieSerializer defaultCookieSerializer = new DefaultCookieSerializer();
        SessionProperties.CookieProperties cookieProperties = sessionProperties.getCookie();

        // Cookie名字，默认为 SESSION
        defaultCookieSerializer.setCookieName(cookieProperties.getCookieName());

        // 域，这允许跨子域共享cookie，默认设置是使用当前域。
        defaultCookieSerializer.setDomainName(cookieProperties.getCookieDomain());

        // Session Cookie 是否使用 Base64
        defaultCookieSerializer.setUseBase64Encoding(cookieProperties.isUseBase64Encoding());

        // Cookie 过期时间
        defaultCookieSerializer.setCookieMaxAge(cookieProperties.getCookieMaxAge());

        // Cookie的路径。
        defaultCookieSerializer.setCookiePath(cookieProperties.getCookiePath());

        return defaultCookieSerializer;
    }

    /**
     * Spring {@link HttpSession} 默认 Redis 序列化程序
     * <p>
     * 名称必须为：springSessionDefaultRedisSerializer
     *
     * @return 返回 Spring {@link HttpSession} 默认 Redis 序列化程序
     * @see RedisHttpSessionConfiguration#setDefaultRedisSerializer(RedisSerializer) 自定义
     * Spring {@link HttpSession} 默认 Redis 序列化程序
     */
    @Bean
    public RedisSerializer<?> springSessionDefaultRedisSerializer() {
        ObjectMapper objectMapper = RedisConfiguration.objectMapper();

        // Web、Security 序列化与反序列化
        // https://github.com/spring-projects/spring-security/issues/4370
        objectMapper.registerModules(new WebServletJackson2Module(), new WebJackson2Module(), new CoreJackson2Module());

        // 可以使用读写JSON
        return new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
    }

}
