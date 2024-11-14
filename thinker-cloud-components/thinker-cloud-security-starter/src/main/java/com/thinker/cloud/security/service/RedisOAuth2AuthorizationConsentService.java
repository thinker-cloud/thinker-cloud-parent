package com.thinker.cloud.security.service;

import com.thinker.cloud.security.repository.RedisAuthorizationConsentRepository;
import com.thinker.cloud.security.repository.entity.RedisAuthorizationConsent;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 基于redis的授权确认服务实现
 *
 * @author admin
 */
@RequiredArgsConstructor
public class RedisOAuth2AuthorizationConsentService implements OAuth2AuthorizationConsentService {

    private final RegisteredClientRepository registeredClientRepository;
    private final RedisAuthorizationConsentRepository authorizationConsentRepository;

    @Override
    public void save(OAuth2AuthorizationConsent authorizationConsent) {
        Assert.notNull(authorizationConsent, "authorizationConsent cannot be null");

        // 如果存在就先删除
        authorizationConsentRepository.findByRegisteredClientIdAndPrincipalName(
                        authorizationConsent.getRegisteredClientId(), authorizationConsent.getPrincipalName())
                .ifPresent(existingConsent -> authorizationConsentRepository.deleteById(existingConsent.getId()));

        // 保存
        RedisAuthorizationConsent entity = toEntity(authorizationConsent);
        entity.setId(UUID.randomUUID().toString());
        authorizationConsentRepository.save(entity);
    }

    @Override
    public void remove(OAuth2AuthorizationConsent authorizationConsent) {
        Assert.notNull(authorizationConsent, "authorizationConsent cannot be null");
        // 如果存在就删除
        authorizationConsentRepository.findByRegisteredClientIdAndPrincipalName(
                        authorizationConsent.getRegisteredClientId(), authorizationConsent.getPrincipalName())
                .ifPresent(existingConsent -> authorizationConsentRepository.deleteById(existingConsent.getId()));
    }

    @Override
    public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
        Assert.hasText(registeredClientId, "registeredClientId cannot be empty");
        Assert.hasText(principalName, "principalName cannot be empty");
        return authorizationConsentRepository.findByRegisteredClientIdAndPrincipalName(
                registeredClientId, principalName).map(this::toObject).orElse(null);
    }

    private OAuth2AuthorizationConsent toObject(RedisAuthorizationConsent authorizationConsent) {
        String registeredClientId = authorizationConsent.getRegisteredClientId();
        RegisteredClient registeredClient = registeredClientRepository.findById(registeredClientId);
        if (registeredClient == null) {
            throw new DataRetrievalFailureException(
                    "The RegisteredClient with id '" + registeredClientId + "' was not found in the RegisteredClientRepository.");
        }

        OAuth2AuthorizationConsent.Builder builder = OAuth2AuthorizationConsent.withId(
                registeredClientId, authorizationConsent.getPrincipalName());
        if (authorizationConsent.getAuthorities() != null) {
            for (String authority : StringUtils.commaDelimitedListToSet(authorizationConsent.getAuthorities())) {
                builder.authority(new SimpleGrantedAuthority(authority));
            }
        }

        return builder.build();
    }

    private RedisAuthorizationConsent toEntity(OAuth2AuthorizationConsent authorizationConsent) {
        RedisAuthorizationConsent entity = new RedisAuthorizationConsent();
        entity.setRegisteredClientId(authorizationConsent.getRegisteredClientId());
        entity.setPrincipalName(authorizationConsent.getPrincipalName());

        Set<String> authorities = new HashSet<>();
        for (GrantedAuthority authority : authorizationConsent.getAuthorities()) {
            authorities.add(authority.getAuthority());
        }
        entity.setAuthorities(StringUtils.collectionToCommaDelimitedString(authorities));

        return entity;
    }

}
