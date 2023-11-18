package com.thinker.cloud.core.aspect.expression;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 默认key 抽取， 优先根据 spel 处理
 *
 * @author admin
 */
@Component
public class ExpressionResolver implements KeyResolver {

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();

    private static final StandardReflectionParameterNameDiscoverer DISCOVERER = new StandardReflectionParameterNameDiscoverer();

    @Override
    public String resolver(JoinPoint point, String key) {
        StandardEvaluationContext context = getEvaluationContext(point);
        if (context == null) {
            return "";
        }
        Expression expression = PARSER.parseExpression(key);
        return expression.getValue(context, String.class);
    }

    @Override
    public String resolver(JoinPoint point, String separator, String... keys) {
        StandardEvaluationContext context = getEvaluationContext(point);
        if (context == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        Arrays.stream(keys).map(PARSER::parseExpression)
                .map(expression -> expression.getValue(context, String.class))
                .forEach(expression -> builder.append(expression).append(separator));
        return builder.deleteCharAt(builder.length() - 1).toString();
    }

    private StandardEvaluationContext getEvaluationContext(JoinPoint point) {
        Object[] arguments = point.getArgs();
        String[] params = DISCOVERER.getParameterNames(getMethod(point));
        if (params == null) {
            return null;
        }
        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int len = 0; len < params.length; len++) {
            context.setVariable(params[len], arguments[len]);
        }
        return context;
    }

    /**
     * 根据切点解析方法信息
     *
     * @param joinPoint 切点信息
     * @return Method 原信息
     */
    private Method getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        if (method.getDeclaringClass().isInterface()) {
            try {
                method = joinPoint.getTarget().getClass()
                        .getDeclaredMethod(joinPoint.getSignature().getName()
                                , method.getParameterTypes());
            } catch (SecurityException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return method;
    }
}
