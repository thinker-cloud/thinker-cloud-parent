package com.thinker.cloud.core.utils;

import cn.hutool.core.util.PageUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlInjectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thinker.cloud.core.constants.CommonConstants;
import com.thinker.cloud.core.exception.FailException;
import com.thinker.cloud.core.model.query.PageQuery;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 分页工具封装
 *
 * @author admin
 */
@UtilityClass
public class MyPageUtil {

    /**
     * 内存分页
     *
     * @param page     mybatis page
     * @param dataList 查询出的所有数据列表
     * @param <T>      数据类型
     * @return 分页后的列表
     */
    public static <T> List<T> page(IPage<T> page, List<T> dataList) {
        if (Objects.isNull(page)) {
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

    public static <T> IPage<T> iPage(PageQuery query, List<T> dataList) {
        IPage<T> page = query.generatePage();
        if (Objects.isNull(query.getPage()) && Objects.isNull(query.getLimit())) {
            return page.setRecords(dataList);
        }
        return iPage(page, dataList);
    }

    public static <T> IPage<T> iPage(IPage<T> page, List<T> dataList) {
        page(page, dataList);
        return page;
    }

    /**
     * 构建分页对象
     *
     * @param query 分页参数
     * @return IPage
     */
    public static <T> IPage<T> generatePage(PageQuery query) {
        // 分页对象
        Page<T> page = new Page<>(query.getPage(), query.getLimit());
        Optional.ofNullable(query.getIsAutoCount()).ifPresent(page::setSearchCount);
        Optional.ofNullable(query.getMaxQueryLimit()).ifPresent(page::setMaxLimit);

        // 排序字段
        String orderField = query.getOrderField();
        if (StringUtils.isNotEmpty(orderField)) {
            // SQL注入检查
            if (SqlInjectionUtils.check(orderField)) {
                throw new FailException("包含非法字符");
            }

            // 正序
            if (CommonConstants.ASC.equalsIgnoreCase(query.getOrder())) {
                return page.addOrder(OrderItem.asc(orderField));
            }

            // 倒序
            return page.addOrder(OrderItem.desc(orderField));
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

        if (totalCount % pageSize == 0) {
            return totalCount / pageSize;
        }

        return totalCount / pageSize + 1;
    }
}
