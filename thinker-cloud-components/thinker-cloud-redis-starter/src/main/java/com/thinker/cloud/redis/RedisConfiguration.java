package com.thinker.cloud.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.thinker.cloud.common.constants.CommonConstants;
import com.thinker.cloud.common.jackson.serializers.datetime.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.*;

/**
 * redis 自动配置类
 *
 * @author admin
 **/
@Slf4j
@EnableCaching
@AllArgsConstructor
@AutoConfigureBefore(RedisAutoConfiguration.class)
@EnableRedisRepositories(basePackages = "com.thinker.cloud",
        enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
public class RedisConfiguration {

    private final RedisProperties redisProperties;

    @Bean
    @Primary
    public LettuceClientConfiguration lettuceClientConfiguration() {
        // Redis连接池配置
        RedisProperties.Pool redisPool = redisProperties.getLettuce().getPool();
        GenericObjectPoolConfig<Object> redisPoolConfig = new GenericObjectPoolConfig<>();
        redisPoolConfig.setMinIdle(redisPool.getMinIdle());
        redisPoolConfig.setMaxIdle(redisPool.getMaxIdle());
        redisPoolConfig.setMaxWait(redisPool.getMaxWait());
        redisPoolConfig.setMaxTotal(redisPool.getMaxActive());
        redisPoolConfig.setTimeBetweenEvictionRuns(Duration.ofMillis(100));
        return LettucePoolingClientConfiguration.builder().poolConfig(redisPoolConfig).build();
    }

    @Bean
    @Primary
    @ConditionalOnBean(LettuceClientConfiguration.class)
    public RedisConnectionFactory redisConnectionFactory(LettuceClientConfiguration lettuceClientConfig) {
        // 集群模式
        RedisProperties.Cluster cluster = redisProperties.getCluster();
        if (Objects.nonNull(cluster) && !CollectionUtils.isEmpty(cluster.getNodes())) {
            return this.redisCluster(lettuceClientConfig, cluster);
        }

        // 哨兵模式
        RedisProperties.Sentinel sentinel = redisProperties.getSentinel();
        if (Objects.nonNull(sentinel) && !CollectionUtils.isEmpty(sentinel.getNodes())) {
            return this.redisSentinel(lettuceClientConfig, sentinel);
        }

        // 单机模式
        return this.redisSingle(lettuceClientConfig);
    }

    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // 1.创建RedisTemplate对象
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // 2.加载Redis配置
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 3.配置key序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setStringSerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        // 4.配置Value序列化 jackson
        ObjectMapper objMapper = objectMapper();
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(objMapper, Object.class);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        // 5.初始化RedisTemplate
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    @Primary
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // 1.创建RedisTemplate对象
        StringRedisTemplate redisTemplate = new StringRedisTemplate();

        // 2.加载Redis配置
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 3.配置key序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        // 4.配置Value序列化
        redisTemplate.setValueSerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(stringRedisSerializer);

        // 5.初始化StringRedisTemplate
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * ObjectMapper
     */
    public static ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setLocale(Locale.CHINA);
        objectMapper.setTimeZone(TimeZone.getTimeZone(CommonConstants.ASIA_SHANGHAI));
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance
                , ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        return objectMapper;
    }

    /**
     * 单机模式
     *
     * @param lettuceClientConfig lettuceClientConfig
     * @return LettuceConnectionFactory
     */
    private LettuceConnectionFactory redisSingle(LettuceClientConfiguration lettuceClientConfig) {
        RedisStandaloneConfiguration redisStandaloneConfig = new RedisStandaloneConfiguration();
        redisStandaloneConfig.setPort(redisProperties.getPort());
        redisStandaloneConfig.setHostName(redisProperties.getHost());
        redisStandaloneConfig.setDatabase(redisProperties.getDatabase());
        redisStandaloneConfig.setUsername(redisProperties.getUsername());
        redisStandaloneConfig.setPassword(RedisPassword.of(redisProperties.getPassword()));

        log.info("redis client is single mode");
        return new LettuceConnectionFactory(redisStandaloneConfig, lettuceClientConfig);
    }

    /**
     * 哨兵模式
     *
     * @param lettuceClientConfig lettuceClientConfig
     * @param sentinel            sentinel
     * @return LettuceConnectionFactory
     */
    private LettuceConnectionFactory redisSentinel(LettuceClientConfiguration lettuceClientConfig, RedisProperties.Sentinel sentinel) {
        RedisSentinelConfiguration redisSentinelConfig = new RedisSentinelConfiguration(
                sentinel.getMaster(), new HashSet<>(sentinel.getNodes()));
        redisSentinelConfig.setDatabase(redisProperties.getDatabase());
        redisSentinelConfig.setUsername(redisProperties.getUsername());
        redisSentinelConfig.setPassword(RedisPassword.of(redisProperties.getPassword()));
        redisSentinelConfig.setSentinelUsername(sentinel.getUsername());
        redisSentinelConfig.setSentinelPassword(RedisPassword.of(sentinel.getPassword()));

        log.info("redis client is sentinel mode");
        return new LettuceConnectionFactory(redisSentinelConfig, lettuceClientConfig);
    }

    /**
     * 集群模式
     *
     * @param lettuceClientConfig lettuceClientConfig
     * @param cluster             cluster
     * @return LettuceConnectionFactory
     */
    private LettuceConnectionFactory redisCluster(LettuceClientConfiguration lettuceClientConfig, RedisProperties.Cluster cluster) {
        RedisClusterConfiguration redisClusterConfig = new RedisClusterConfiguration(cluster.getNodes());
        Optional.ofNullable(cluster.getMaxRedirects()).ifPresent(redisClusterConfig::setMaxRedirects);
        redisClusterConfig.setUsername(redisProperties.getUsername());
        redisClusterConfig.setPassword(RedisPassword.of(redisProperties.getPassword()));

        log.info("redis client is cluster mode");
        return new LettuceConnectionFactory(redisClusterConfig, lettuceClientConfig);
    }
}
