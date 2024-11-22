package com.thinker.cloud.db.plugins;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.*;

/**
 * 分页处理拦截
 * <p>
 * 返回List时，将返回的List动态set到page中的records中
 *
 * @author admin
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@SuppressWarnings({"unchecked", "rawtypes"})
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
})
public class PageHandlerInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object result = invocation.proceed();
        Optional.ofNullable(invocation.getArgs())
                .flatMap(args -> Arrays.stream(args)
                        .map(this::getPage)
                        .filter(Objects::nonNull)
                        .findFirst())
                .ifPresent(page -> page.setRecords((List) result));
        return result;
    }

    private IPage<?> getPage(Object params) {
        if (params instanceof IPage) {
            return (IPage<?>) params;
        } else if (params instanceof Map) {
            Map<String, Object> pageParamMap = (Map) params;
            for (Object param : pageParamMap.values()) {
                if (param instanceof IPage) {
                    return (IPage<?>) param;
                }
            }
        }
        return null;
    }
}
