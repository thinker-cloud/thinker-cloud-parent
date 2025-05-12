package com.thinker.cloud.db.plugins;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
@Intercepts(@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}))
public class PageHandlerInterceptor implements Interceptor {

    private static final String MP_COUNT_KEY = "_mpCount";
    private final Map<String, Long> cacheTotalMap = new ConcurrentHashMap<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();

        // 检查是否是 count 查询
        MappedStatement statement = (MappedStatement) args[0];
        if (statement.getId().endsWith(MP_COUNT_KEY)) {
            return invocation.proceed();
        }

        Object params = args[1];
        IPage page = getPage(params);
        Object result = invocation.proceed();
        if (page != null) {
            page.setRecords((List) result);

            // 缓存总数处理
            caCheTotalHandle(args, params, page);
        }
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

    /**
     * 获得查询条件列表
     *
     * @param params params
     * @return List<Object>
     */
    private List<Object> queryConditions(Object params) {
        Set<Object> paramsSet = Sets.newHashSet();
        if (params instanceof Map) {
            Map<String, Object> pageParamMap = (Map) params;
            paramsSet.addAll(pageParamMap.values().stream()
                    .filter(param -> !(param instanceof IPage)).toList());
        }
        return Lists.newArrayList(paramsSet);
    }

    /**
     * 缓存总数处理
     *
     * @param args   args
     * @param params params
     * @param page   page
     */
    private void caCheTotalHandle(Object[] args, Object params, IPage page) {
        // 缓存total，解决mybatis引入缓存后total为0的问题
        String pageTotalKey;
        try {
            String query = JSON.toJSONString(queryConditions(params));
            pageTotalKey = ((MappedStatement) args[0]).getId() + query;
        } catch (ClassCastException e) {
            log.error("分页total缓存处理异常，请优化代码");
            return;
        }
        if (!page.getRecords().isEmpty() && page.getTotal() == 0 && cacheTotalMap.containsKey(pageTotalKey)) {
            page.setTotal(cacheTotalMap.get(pageTotalKey));
        } else {
            cacheTotalMap.put(pageTotalKey, page.getTotal());
        }
    }
}
