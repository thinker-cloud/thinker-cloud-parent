package com.thinker.cloud.core.constants;

/**
 * 公共常量类
 *
 * @author admin
 **/
public class CommonConstant {

    /**
     * 排序方式：升序
     */
    public static final String ASC = "asc";

    /**
     * 排序方式：倒序
     */
    public static final String DESC = "desc";

    /**
     * 内部feign调用 Header标识，用于过滤接口鉴权相关处理
     */
    public static final String INNER_HEADER_KEY = "from_in";

    /**
     * 内部feign调用 Header标识值，用于过滤接口鉴权相关处理
     */
    public static final String INNER_IDENT_VALUE = "inner";

}
