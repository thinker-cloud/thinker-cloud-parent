package com.thinker.cloud.redis.cache.generator;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.thinker.cloud.core.aspect.expression.ExpressionResolver;
import com.thinker.cloud.core.utils.DeflaterUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.lang.NonNull;

/**
 * 自定义缓存key生成器
 *
 * @author admin
 **/
@Slf4j
@AllArgsConstructor
public class CacheKeyGenerator {

    private final ExpressionResolver keyResolver;

    /**
     * 生成缓存key
     *
     * @param joinPoint 切面断点
     * @param prefix    缓存前缀
     * @param keys      指定的el表达式缓存key
     * @return 生成的缓存key
     */
    public String generator(@NonNull JoinPoint joinPoint, @NonNull String prefix, String... keys) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String keyPrefix = StrUtil.format("{}:{}.{}:", prefix
                , signature.getDeclaringTypeName(), signature.getMethod().getName());

        // 未指定key，则使用 url + 参数列表作为key
        if (ArrayUtil.isEmpty(keys)) {
            String argStr = JSONArray.toJSONString(joinPoint.getArgs());
            return keyPrefix + DeflaterUtil.zipString(argStr);
        }

        // 使用jstl 规则区分
        return keyPrefix + keyResolver.resolver(joinPoint, keys);
    }

    /**
     * 生成缓存key
     *
     * @param joinPoint  切面断点
     * @param prefix     缓存前缀
     * @param keys       指定的el表达式缓存key
     * @param ignoreKeys 忽略参数字段key列表
     * @return 生成的缓存key
     */
    public String generator(@NonNull JoinPoint joinPoint, @NonNull String prefix, String[] keys, String[] ignoreKeys) {
        // 指定了 Keys 或者 未指定 ignoreKeys
        if (ArrayUtil.isNotEmpty(keys) || ArrayUtil.isEmpty(ignoreKeys)) {
            return this.generator(joinPoint, prefix, keys);
        }

        // 正则替换需要排除的字段
        String argStr = JSONArray.toJSONString(joinPoint.getArgs());
        for (String ignoreKey : ignoreKeys) {
            argStr = argStr.replaceAll(ignoreKey + "=.*?, ", "");
        }

        // 将排除后的所有参数压缩生成缓存key
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String keyPrefix = StrUtil.format("{}:{}.{}:", prefix
                , signature.getDeclaringTypeName(), signature.getMethod().getName());
        return keyPrefix + DeflaterUtil.zipString(argStr);
    }
}
