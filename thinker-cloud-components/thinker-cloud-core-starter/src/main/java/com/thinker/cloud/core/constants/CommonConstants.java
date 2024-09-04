package com.thinker.cloud.core.constants;

/**
 * 公共常量类
 *
 * @author admin
 **/
public interface CommonConstants {

    /**
     * 排序方式：升序
     */
    String ASC = "asc";

    /**
     * 排序方式：倒序
     */
    String DESC = "desc";

    /**
     * 编码
     */
    String UTF8 = "UTF-8";

    /**
     * header中租户标识
     */
    String TENANT = "Tenant";

    /**
     * Inner 内部
     */
    String FROM_IN = "Y";

    /**
     * Inner 内部标识
     */
    String FROM = "from";

    /**
     * 默认租户
     */
    Long DEFAULT_TENANT = 1L;

    /**
     * 时区
     */
    String ASIA_SHANGHAI = "Asia/Shanghai";

    /**
     * JSON 资源
     */
    String APPLICATION_JSON_UTF8 = "application/json;charset=utf-8";
}
