package com.thinker.cloud.db.tenant;

import com.thinker.cloud.core.utils.tenant.TenantContextHolder;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * 租户运行时代理<br/>
 * 这是一个工具类，用于切换租户运行时，保护租户ID上下文<br/>
 * 下面这段代码演示问题所在 <pre>
 *     void methodA(){
 *         // 因为某些特殊原因，需要手动指定租户
 *         TenantContextHolder.setTenantId(1);
 *         // do something ...
 *     }
 *     void methodB(){
 *         // 因为某些特殊原因，需要手动指定租户
 *         TenantContextHolder.setTenantId(2);
 *         methodA();
 *         // 此时租户ID已经变成 1
 *         // do something ...
 *     }
 * </pre> 嵌套设置租户ID会导致租户上下文难以维护,并且很难察觉，容易导致数据错乱。 推荐的写法： <pre>
 *     void methodA(){
 *         TenantBroker.RunAs(1,() -> {
 *             // do something ...
 *         });
 *     }
 *     void methodB(){
 *         TenantBroker.RunAs(2,() -> {
 *              methodA();
 *             // do something ...
 *         });
 *     }
 * </pre>
 *
 * @author admin
 */
@Slf4j
@UtilityClass
public class TenantBroker {

    @FunctionalInterface
    public interface RunAs<T> {

        /**
         * 执行业务逻辑
         *
         * @param tenantId tenantId
         * @throws Exception Exception
         */
        void run(T tenantId) throws Exception;

    }

    @FunctionalInterface
    public interface ApplyAs<T, R> {

        /**
         * 执行业务逻辑,返回一个值
         *
         * @param tenantId tenantId
         * @return R
         * @throws Exception Exception
         */
        R apply(T tenantId) throws Exception;

    }

    /**
     * 以某个租户的身份运行
     *
     * @param tenant 租户ID
     * @param func   func
     */
    public void runAs(Long tenant, RunAs<Long> func) {
        final Long pre = TenantContextHolder.getTenantId();
        try {
            log.trace("TenantBroker.runAs 切换租户{} -> {}", pre, tenant);
            TenantContextHolder.setTenantId(tenant);
            func.run(tenant);
        } catch (Exception e) {
            throw new TenantBrokerException(e.getMessage(), e);
        } finally {
            log.trace("TenantBroker.runAs 还原租户{} <- {}", pre, tenant);
            TenantContextHolder.setTenantId(pre);
        }
    }

    /**
     * 以某个租户的身份运行
     *
     * @param tenant 租户ID
     * @param func   func
     * @param <T>    返回数据类型
     * @return T
     */
    public <T> T applyAs(Long tenant, ApplyAs<Long, T> func) {
        final Long pre = TenantContextHolder.getTenantId();
        try {
            log.trace("TenantBroker.applyAs 切换租户{} -> {}", pre, tenant);
            TenantContextHolder.setTenantId(tenant);
            return func.apply(tenant);
        } catch (Exception e) {
            throw new TenantBrokerException(e.getMessage(), e);
        } finally {
            log.trace("TenantBroker.applyAs 还原租户{} <- {}", pre, tenant);
            TenantContextHolder.setTenantId(pre);
        }
    }

    /**
     * 以某个租户的身份运行
     *
     * @param supplier supplier
     * @param func     func
     */
    public void runAs(Supplier<Long> supplier, RunAs<Long> func) {
        runAs(supplier.get(), func);
    }

    /**
     * 以某个租户的身份运行
     *
     * @param supplier supplier
     * @param func     func
     * @param <T>      返回数据类型
     * @return T
     */
    public <T> T applyAs(Supplier<Long> supplier, ApplyAs<Long, T> func) {
        return applyAs(supplier.get(), func);
    }


    /**
     * 租户代理异常
     */
    public static class TenantBrokerException extends RuntimeException {

        public TenantBrokerException(String message, Throwable cause) {
            super(message, cause);
        }

        public TenantBrokerException(Throwable cause) {
            super(cause);
        }
    }
}
