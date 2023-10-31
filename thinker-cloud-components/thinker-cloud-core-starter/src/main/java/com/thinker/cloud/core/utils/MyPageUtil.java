package com.thinker.cloud.core.utils;

import cn.hutool.core.util.PageUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thinker.cloud.core.constants.CommonConstants;
import com.thinker.cloud.core.model.query.PageQuery;
import com.thinker.cloud.core.xss.SqlFilter;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * 分页工具封装
 *
 * @author admin
 */
@UtilityClass
public class MyPageUtil {

    /**
     * java程序分页
     *
     * @param page     mybatis page
     * @param dataList 查询出的所有数据列表
     * @param <T>      数据类型
     * @return 分页后的列表
     */
    @SuppressWarnings("UnusedReturnValue")
    public static <T> List<T> page(IPage<T> page, List<T> dataList) {
        if (page == null) {
            return dataList;
        }
        PageUtil.setFirstPageNo(1);
        page.setTotal(dataList.size());
        if (page.getCurrent() > page.getPages()) {
            page.setCurrent(1);
        }
        int current = (int) page.getCurrent(), pageSize = (int) page.getSize();
        int[] startEnd = PageUtil.transToStartEnd(current, pageSize);
        List<T> pageResult = dataList.subList(startEnd[0], Math.min(startEnd[1], dataList.size()));
        page.setRecords(pageResult);
        return pageResult;
    }

    public static <T> IPage<T> iPage(PageQuery params, List<T> dataList) {
        IPage<T> page = params.generatePage();
        if (Objects.isNull(params.getPage()) && Objects.isNull(params.getLimit())) {
            return page.setRecords(dataList);
        }
        return iPage(page, dataList);
    }

    public static <T> IPage<T> iPage(IPage<T> page, List<T> dataList) {
        page(page, dataList);
        return page;
    }

    public static <T> IPage<T> generatePage(PageQuery queryParams) {
        return generatePage(queryParams, null, false);
    }

    /**
     * 构建分页对象
     *
     * @param queryParams       分页参数
     * @param defaultOrderField 默认排序字段
     * @param isAsc             是否默认升序
     * @return IPage
     */
    public static <T> IPage<T> generatePage(PageQuery queryParams, String defaultOrderField, boolean isAsc) {
        // 分页对象
        Page<T> page = new Page<>(queryParams.getPage(), queryParams.getLimit());

        // 防止SQL注入（因为sidx、order是通过拼接SQL实现排序的，会有SQL注入风险）
        String orderField = SqlFilter.sqlInject(queryParams.getOrderField());
        String order = queryParams.getOrder();

        // 前端字段排序
        if (StringUtils.isNotEmpty(orderField)) {
            if (CommonConstants.ASC.equalsIgnoreCase(order)) {
                return page.addOrder(OrderItem.asc(orderField));
            } else {
                return page.addOrder(OrderItem.desc(orderField));
            }
        }

        // 没有排序字段，则不排序
        if (StringUtils.isBlank(defaultOrderField)) {
            return page;
        }

        // 默认排序
        if (isAsc) {
            page.addOrder(OrderItem.asc(defaultOrderField));
        } else {
            page.addOrder(OrderItem.desc(defaultOrderField));
        }
        return page;
    }

    /**
     * 根据分页参数截取list
     *
     * @param list  数据列表
     * @param page  page
     * @param limit limit
     * @param <T>   <T>
     * @return List<T>
     */
    public static <T> List<T> subList(List<T> list, long page, long limit) {
        long pageSize = Math.min(page * limit, list.size());
        return list.subList((int) ((page - 1) * limit), (int) pageSize);
    }

    /**
     * 根据总数计算总页数
     *
     * @param totalCount 总数
     * @param pageSize   每页数
     * @return 总页数
     */
    public static long totalPage(long totalCount, long pageSize) {
        if (pageSize == 0) {
            return 0;
        }
        return totalCount % pageSize == 0 ? (totalCount / pageSize) : (totalCount / pageSize + 1);
    }
}
