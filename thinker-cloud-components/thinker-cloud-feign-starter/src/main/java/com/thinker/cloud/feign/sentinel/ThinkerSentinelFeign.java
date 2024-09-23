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

package com.thinker.cloud.feign.sentinel;

import com.alibaba.cloud.sentinel.feign.SentinelContractHolder;
import feign.Contract;
import feign.Feign;
import feign.InvocationHandlerFactory;
import feign.Target;
import org.springframework.beans.BeansException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 支持自动降级注入 重写 {@link com.alibaba.cloud.sentinel.feign.SentinelFeign}
 *
 * @author admin
 */
public final class ThinkerSentinelFeign {

    private ThinkerSentinelFeign() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends Feign.Builder implements ApplicationContextAware {

        private Contract contract = new Contract.Default();


        private FeignClientFactory feignClientFactory;

        @Override
        public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
            this.feignClientFactory = applicationContext.getBean(FeignClientFactory.class);
        }

        @Override
        public Feign.Builder invocationHandlerFactory(InvocationHandlerFactory invocationHandlerFactory) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Builder contract(Contract contract) {
            this.contract = contract;
            return this;
        }

        @Override
        public Feign internalBuild() {
            super.invocationHandlerFactory(new InvocationHandlerFactory() {
                @Override
                public InvocationHandler create(Target target, Map<Method, MethodHandler> dispatch) {
                    // 查找 FeignClient 上的 降级策略
                    FeignClient feignClient = AnnotationUtils.findAnnotation(target.type(), FeignClient.class);
                    if (feignClient == null) {
                        return new ThinkerSentinelInvocationHandler(target, dispatch);
                    }

                    Class<?> fallback = feignClient.fallback();
                    Class<?> fallbackFactory = feignClient.fallbackFactory();

                    String beanName = feignClient.contextId();
                    if (!StringUtils.hasText(beanName)) {
                        beanName = feignClient.name();
                    }

                    Object fallbackInstance;
                    FallbackFactory<?> fallbackFactoryInstance;
                    if (void.class != fallback) {
                        fallbackInstance = getFromContext(beanName, "fallback", fallback, target.type());
                        return new ThinkerSentinelInvocationHandler(target, dispatch, new FallbackFactory.Default<>(fallbackInstance));
                    }

                    if (void.class != fallbackFactory) {
                        fallbackFactoryInstance = (FallbackFactory<?>) getFromContext(beanName
                                , "fallbackFactory", fallbackFactory, FallbackFactory.class);
                        return new ThinkerSentinelInvocationHandler(target, dispatch, fallbackFactoryInstance);
                    }
                    return new ThinkerSentinelInvocationHandler(target, dispatch);
                }

                private Object getFromContext(String name, String type, Class<?> fallbackType, Class<?> targetType) {
                    Object fallbackInstance = feignClientFactory.getInstance(name, fallbackType);
                    if (fallbackInstance == null) {
                        throw new IllegalStateException(String.format("No %s instance of type %s found for feign client %s"
                                , type, fallbackType, name));
                    }

                    if (!targetType.isAssignableFrom(fallbackType)) {
                        throw new IllegalStateException(String.format("Incompatible %s instance. Fallback/fallbackFactory of type %s is not assignable to %s for feign client %s"
                                , type, fallbackType, targetType, name));
                    }
                    return fallbackInstance;
                }
            });

            super.contract(new SentinelContractHolder(contract));
            return super.internalBuild();
        }
    }
}
