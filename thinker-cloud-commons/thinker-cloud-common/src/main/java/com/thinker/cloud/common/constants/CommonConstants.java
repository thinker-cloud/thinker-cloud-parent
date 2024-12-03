package com.thinker.cloud.common.constants;

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
     * 树根父节点id
     */
    Long TREE_ROOT = -1L;

    /**
     * 默认租户
     */
    Long DEFAULT_TENANT = 1L;

    /**
     * 正常
     */
    Integer STATUS_NORMAL = 1;

    /**
     * 锁定
     */
    Integer STATUS_LOCK = -1;

    /**
     * 过期
     */
    Integer STATUS_EXPIRED = 2;

    /**
     * 时区
     */
    String ASIA_SHANGHAI = "Asia/Shanghai";

    /**
     * JSON 资源
     */
    String APPLICATION_JSON_UTF8 = "application/json;charset=utf-8";

    /**
     * 客户端授权类型
     */
    String AUTH_TYPE = "X-Client-Auth";

    /**
     * 请求开始时间
     */
    String REQUEST_START_TIME = "REQUEST-START-TIME";
}
