package com.thinker.cloud.security.repository;

import com.thinker.cloud.security.repository.entity.RedisRegisteredClient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 基于Redis的客户端repository
 *
 * @author admin
 */
@Repository
public interface RedisOauthClientRepository extends CrudRepository<RedisRegisteredClient, String> {

    /**
     * 根据客户端Id查询客户端信息
     *
     * @param clientId 客户端id
     * @return 客户端信息
     */
    Optional<RedisRegisteredClient> findByClientId(String clientId);

}
