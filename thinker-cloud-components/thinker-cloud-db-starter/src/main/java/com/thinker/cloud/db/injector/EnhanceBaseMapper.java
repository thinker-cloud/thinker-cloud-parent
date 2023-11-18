package com.thinker.cloud.db.injector;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Collection;

/**
 * 扩展通用 Mapper 批量插入
 *
 * @author admin
 */
public interface EnhanceBaseMapper<T> extends BaseMapper<T> {

    /**
     * 批量插入
     *
     * @param entityList 实体列表
     * @return 影响行数
     */
    Integer insertBatchSomeColumn(Collection<T> entityList);

}
