package com.thinker.cloud.redis;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;

import java.util.Objects;

/**
 * Redisson 配置类
 *
 * @author admin
 **/
@Slf4j
@AllArgsConstructor
@AutoConfigureBefore(RedisAutoConfiguration.class)
public class RedissonConfiguration {

    private final RedisProperties redisProperties;
    private static final String ADDRESS_PREFIX = "redis://";

    @Bean
    public RedissonClient redissonClient() {
        // 集群模式
        RedisProperties.Cluster cluster = redisProperties.getCluster();
        if (Objects.nonNull(cluster) && CollectionUtil.isNotEmpty(cluster.getNodes())) {
            return this.redissonCluster();
        }

        // 哨兵模式
        RedisProperties.Sentinel sentinel = redisProperties.getSentinel();
        if (Objects.nonNull(sentinel) && CollectionUtil.isNotEmpty(sentinel.getNodes())) {
            return this.redissonSentinel();
        }

        // 单机模式
        return this.redissonSingle();
    }

    /**
     * 单机模式
     */
    private RedissonClient redissonSingle() {
        int port = redisProperties.getPort();
        String host = redisProperties.getHost();
        String password = redisProperties.getPassword();

        // 声明一个配置类
        Config config = new Config();
        SingleServerConfig serverConfig = config.useSingleServer();
        serverConfig.setAddress(ADDRESS_PREFIX + host + ":" + port);

        // 判断密码
        if (StrUtil.isNotBlank(password)) {
            serverConfig.setPassword(password);
        }

        log.info("redisson client is single mode");
        return Redisson.create(config);
    }


    /**
     * 哨兵模式
     */
    private RedissonClient redissonSentinel() {
        RedisProperties.Sentinel sentinel = redisProperties.getSentinel();
        String password = redisProperties.getPassword();

        // 声明一个配置类
        Config config = new Config();

        SentinelServersConfig sentinelServersConfig = config.useSentinelServers();
        sentinelServersConfig.setMasterName(sentinel.getMaster());
        sentinelServersConfig.setDatabase(redisProperties.getDatabase());
        sentinelServersConfig.setSentinelUsername(sentinel.getUsername());
        sentinelServersConfig.setSentinelPassword(sentinel.getPassword());

        // 判断密码
        if (StrUtil.isNotBlank(password)) {
            sentinelServersConfig.setPassword(password);
        }

        String[] nodes = sentinel.getNodes().stream()
                .map(node -> ADDRESS_PREFIX + node)
                .toArray(String[]::new);

        // 添加redis节点
        sentinelServersConfig.addSentinelAddress(nodes);

        log.info("redisson client is sentinel mode");
        return Redisson.create(config);
    }

    /**
     * 集群模式
     */
    private RedissonClient redissonCluster() {
        String password = redisProperties.getPassword();

        // 声明一个配置类
        Config config = new Config();
        ClusterServersConfig clusterServersConfig = config.useClusterServers();

        // 判断密码
        if (StrUtil.isNotBlank(password)) {
            clusterServersConfig.setPassword(password);
        }

        // 添加redis节点
        String[] nodes = redisProperties.getCluster().getNodes().stream()
                .map(node -> ADDRESS_PREFIX + node)
                .toArray(String[]::new);
        clusterServersConfig.addNodeAddress(nodes);

        log.info("redisson client is cluster mode");
        return Redisson.create(config);
    }
}
