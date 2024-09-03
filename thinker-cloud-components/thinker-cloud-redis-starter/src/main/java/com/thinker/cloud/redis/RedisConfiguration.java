package com.thinker.cloud.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thinker.cloud.core.constants.CommonConstants;
import com.thinker.cloud.core.jackson.serializers.datetime.JavaTimeModule;
import lombok.AllArgsConstructor;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
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
@EnableCaching
@Configuration
@AllArgsConstructor
@AutoConfigureBefore(RedisAutoConfiguration.class)
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
            RedisClusterConfiguration redisClusterConfig = new RedisClusterConfiguration(cluster.getNodes());
            redisClusterConfig.setUsername(redisProperties.getUsername());
            redisClusterConfig.setMaxRedirects(cluster.getMaxRedirects());
            redisClusterConfig.setPassword(RedisPassword.of(redisProperties.getPassword()));
            return new LettuceConnectionFactory(redisClusterConfig, lettuceClientConfig);
        }

        // 哨兵模式
        RedisProperties.Sentinel sentinel = redisProperties.getSentinel();
        if (Objects.nonNull(sentinel) && !CollectionUtils.isEmpty(sentinel.getNodes())) {
            RedisSentinelConfiguration redisSentinelConfig = new RedisSentinelConfiguration(
                    sentinel.getMaster(), new HashSet<>(sentinel.getNodes()));
            redisSentinelConfig.setUsername(sentinel.getUsername());
            redisSentinelConfig.setDatabase(redisProperties.getDatabase());
            redisSentinelConfig.setPassword(RedisPassword.of(redisProperties.getPassword()));
            return new LettuceConnectionFactory(redisSentinelConfig, lettuceClientConfig);
        }

        // 单机模式
        RedisStandaloneConfiguration redisStandaloneConfig = new RedisStandaloneConfiguration();
        redisStandaloneConfig.setDatabase(redisProperties.getDatabase());
        redisStandaloneConfig.setHostName(redisProperties.getHost());
        redisStandaloneConfig.setPort(redisProperties.getPort());
        redisStandaloneConfig.setPassword(RedisPassword.of(redisProperties.getPassword()));
        return new LettuceConnectionFactory(redisStandaloneConfig, lettuceClientConfig);
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
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        // 4.配置Value序列化 jackson
        ObjectMapper objMapper = new ObjectMapper();
        objMapper.setLocale(Locale.CHINA);
        objMapper.registerModule(new JavaTimeModule());
        objMapper.setTimeZone(TimeZone.getTimeZone(CommonConstants.ASIA_SHANGHAI));
        objMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objMapper.activateDefaultTyping(objMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
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

    @Bean
    @ConditionalOnMissingBean(CacheManagerCustomizers.class)
    public CacheManagerCustomizers cacheManagerCustomizers(ObjectProvider<List<CacheManagerCustomizer<?>>> provider) {
        return new CacheManagerCustomizers(provider.getIfAvailable());
    }
}
