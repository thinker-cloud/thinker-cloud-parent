package com.thinker.cloud.security.component;

import com.thinker.cloud.common.enums.ResponseCode;
import com.thinker.cloud.security.annotation.HasPermission;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;

/**
 * 接口权限校验
 *
 * @author admin
 **/
@Slf4j
@Aspect
@AllArgsConstructor
public class HasPermissionAspect {

    private final PermissionService permissionService;

    @Before("@annotation(hasPermission)")
    public void hasPermission(HasPermission hasPermission) {
        // 校验接口权限
        if (!permissionService.hasPermission(hasPermission.value())) {
            throw new AccessDeniedException(ResponseCode.UNAUTHORIZED.getDesc());
        }
    }
}
