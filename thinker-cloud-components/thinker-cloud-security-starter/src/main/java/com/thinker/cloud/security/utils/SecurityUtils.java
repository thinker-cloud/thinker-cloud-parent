/*
 * Copyright (c) 2020 pig4cloud Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thinker.cloud.security.utils;

import cn.hutool.core.util.StrUtil;
import com.thinker.cloud.security.constants.SecurityConstants;
import com.thinker.cloud.security.userdetail.DigitUser;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

/**
 * 安全工具类
 *
 * @author L.cm
 */
@Slf4j
@UtilityClass
public class SecurityUtils {

    /**
     * 获取Authentication
     */
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取用户
     */
    public DigitUser getUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof DigitUser) {
            return (DigitUser) principal;
        }
        return null;
    }

    /**
     * 获取用户
     */
    public DigitUser getUser() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }
        return getUser(authentication);
    }

    /**
     * 获取用户id
     *
     * @return Long
     */
    public Long getUserId() {
        return Optional.ofNullable(getUser()).map(DigitUser::getId).orElse(null);
    }

    /**
     * 获取组织架构id
     *
     * @return Long
     */
    public Long getOrganizationId() {
        return Optional.ofNullable(getUser()).map(DigitUser::getOrganizationId).orElse(null);
    }

    /**
     * 获取所属租户id
     *
     * @return Long
     */
    public Long getTenantId() {
        return Optional.ofNullable(getUser()).map(DigitUser::getTenantId).orElse(null);
    }

    /**
     * 获取用户角色信息
     *
     * @return 角色集合
     */
    public List<Long> getRoles() {
        Authentication authentication = getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        List<Long> roleIds = new ArrayList<>();
        authorities.stream()
                .filter(granted -> StrUtil.startWith(granted.getAuthority(), SecurityConstants.AUTH_ROLE))
                .forEach(granted -> {
                    String id = StrUtil.removePrefix(granted.getAuthority(), SecurityConstants.AUTH_ROLE);
                    roleIds.add(Long.parseLong(id));
                });
        return roleIds;
    }

}
