package com.thinker.cloud.security.component;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.google.common.collect.Sets;
import com.thinker.cloud.security.annotation.Inner;
import com.thinker.cloud.security.constants.SecurityConstants;
import com.thinker.cloud.security.properties.SecurityProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.UrlPathHelper;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 对外直接暴露URL解析器
 *
 * @author admin
 */
@Slf4j
@RequiredArgsConstructor
public class PermitAllUrlMatcher implements InitializingBean, RequestMatcher {

    private static final UrlPathHelper PATH_HELPER = new UrlPathHelper();
    private static final PathMatcher PATH_MATCHER = new AntPathMatcher();
    private static final Pattern PATTERN = Pattern.compile("\\{(.*?)}");
    /**
     * 默认白名单接口
     */
    private static final Set<String> DEFAULT_IGNORE_URLS = Sets.newHashSet(
            "/favicon.ico", "/error", "/actuator/**", "/webjars/**", "/css/**"
    );

    private final SecurityProperties securityProperties;

    @Override
    public void afterPropertiesSet() {
        RequestMappingHandlerMapping mapping = SpringUtil.getBean(SecurityConstants.REQUEST_MAPPING_HANDLER);
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();

        for (RequestMappingInfo info : map.keySet()) {
            HandlerMethod handlerMethod = map.get(info);

            // 1. 首先获取类上边 @Inner 注解
            Inner controller = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), Inner.class);

            // 2. 当类上不包含 @Inner 注解则获取该方法的注解
            if (controller == null) {
                Inner method = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), Inner.class);
                Optional.ofNullable(method)
                        .map(var -> info.getPathPatternsCondition())
                        .map(PathPatternsRequestCondition::getPatternValues)
                        .ifPresent(patterns -> patterns.forEach(url -> this.filterPath(url, info, map)));
                continue;
            }

            // 3. 当类上包含 @Inner 注解 判断handlerMethod 是否包含在 inner 类中
            Class<?> beanType = handlerMethod.getBeanType();
            Method[] methods = beanType.getDeclaredMethods();
            if (ArrayUtil.contains(methods, handlerMethod.getMethod())) {
                Optional.ofNullable(info.getPathPatternsCondition())
                        .map(PathPatternsRequestCondition::getPatternValues)
                        .ifPresent(patterns -> patterns.forEach(url -> this.filterPath(url, info, map)));
            }
        }
    }

    @Override
    public MatchResult matcher(HttpServletRequest request) {
        var lookupPath = PATH_HELPER.getLookupPathForRequest(request);
        // 默认白名单接口
        for (String defaultIgnoreUrl : DEFAULT_IGNORE_URLS) {
            if (PATH_MATCHER.match(defaultIgnoreUrl, lookupPath)) {
                Map<String, String> variables = PATH_MATCHER.extractUriTemplateVariables(defaultIgnoreUrl, lookupPath);
                return MatchResult.match(variables);
            }
        }

        // Nacos配置白名单接口
        for (String pattern : securityProperties.getIgnoreUrls()) {
            if (PATH_MATCHER.match(pattern, lookupPath)) {
                Map<String, String> variables = PATH_MATCHER.extractUriTemplateVariables(pattern, lookupPath);
                return MatchResult.match(variables);
            }
        }
        return MatchResult.notMatch();
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        var lookupPath = PATH_HELPER.getLookupPathForRequest(request);
        // 默认白名单接口
        if (DEFAULT_IGNORE_URLS.stream().anyMatch(this.pathMatch(lookupPath))) {
            return true;
        }

        // Nacos配置白名单接口
        Set<String> ignoreUrls = securityProperties.getIgnoreUrls();
        return ignoreUrls.stream().anyMatch(this.pathMatch(lookupPath));
    }

    /**
     * 过滤 Inner 设置
     * <p>
     * 0. 暴露安全检查 1. 路径转换： 如果为restful(/xx/{xx}) --> /xx/* ant 表达式 2.
     * 构建表达式：允许暴露的接口|允许暴露的方法类型,允许暴露的方法类型 URL|GET,POST,DELETE,PUT
     * </p>
     *
     * @param url  mapping路径
     * @param info 请求犯法
     * @param map  路由映射信息
     */
    private void filterPath(String url, RequestMappingInfo info, Map<RequestMappingInfo, HandlerMethod> map) {
        // inner安全检查
        if (securityProperties.getInnerCheck()) {
            security(url, info, map);
        }

        List<String> methodList = info.getMethodsCondition().getMethods().stream()
                .map(RequestMethod::name).collect(Collectors.toList());
        String resultUrl = ReUtil.replaceAll(url, PATTERN, "*");
        if (CollUtil.isEmpty(methodList)) {
            DEFAULT_IGNORE_URLS.add(resultUrl);
        } else {
            DEFAULT_IGNORE_URLS.add(String.format("%s|%s", resultUrl, CollUtil.join(methodList, StrUtil.COMMA)));
        }
    }

    /**
     * 针对PathVariable 请求安全检查。增加启动好使影响启动效率 请注意
     *
     * @param url 接口路径
     * @param rq  当前请求的元信息
     * @param map springmvc 接口列表
     */
    private void security(String url, RequestMappingInfo rq, Map<RequestMappingInfo, HandlerMethod> map) {
        // 判断 URL 是否是 rest path 形式
        if (!StrUtil.containsAny(url, StrUtil.DELIM_START, StrUtil.DELIM_END)) {
            return;
        }

        for (RequestMappingInfo info : map.keySet()) {
            Set<RequestMethod> methods = info.getMethodsCondition().getMethods();
            // 如果请求方法不匹配跳过
            if (!CollUtil.containsAny(methods, rq.getMethodsCondition().getMethods())) {
                continue;
            }

            // 如果请求方法路径匹配
            Set<String> patterns = Objects.requireNonNull(info.getPathPatternsCondition()).getPatternValues();
            for (String pattern : patterns) {
                // 跳过自身
                if (StrUtil.equals(url, pattern)) {
                    continue;
                }

                if (PATH_MATCHER.match(url, pattern)) {
                    HandlerMethod rqMethod = map.get(rq);
                    HandlerMethod infoMethod = map.get(info);
                    log.error("过滤接口权限 @Inner 注解 ==> {}.{} 存在其他接口额外暴露风险 ==> {}.{} 请检查确认", rqMethod.getBeanType().getName(),
                            rqMethod.getMethod().getName(), infoMethod.getBeanType().getName(),
                            infoMethod.getMethod().getName());
                }
            }
        }
    }

    private Predicate<String> pathMatch(String lookupPath) {
        return pattern -> PATH_MATCHER.match(pattern, lookupPath);
    }
}
