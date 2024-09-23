package ${package.Mapper};

import ${superMapperClassPackage};
import com.baomidou.mybatisplus.core.metadata.IPage;
<#if cfg.isDatabaseMasterSlaveMode>
import com.thinker.cloud.db.dynamic.datasource.annotation.Slave
</#if>
import ${voPackage}.${voName};
import ${entityPackage}.${entityName};
import ${queryPackage}.${queryName};
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

/**
 * ${table.comment!}
 *
 * @author ${author}
 * @since ${date}
 */
@Mapper
<#if kotlin>
interface ${table.mapperName} : ${superMapperClass}<${entityName}>
<#else>
public interface ${table.mapperName} extends ${superMapperClass}<${entityName}> {

    /**
     * 根据query分页查询
     *
     * @param page  分页对象
     * @param query 查询条件
     * @return 分页列表
     */
    <#if cfg.isDatabaseMasterSlaveMode>
    @Slave
    </#if>
    List<${voName}> page(@Param("page") IPage<${voName}> page, @Param("query") ${queryName} query);

    /**
     * 根据query查询列表
     *
     * @param query 查询条件
     * @return 列表
     */
    <#if cfg.isDatabaseMasterSlaveMode>
    @Slave
    </#if>
    List<${voName}> list(@Param("query") ${queryName} query);

    /**
     * 根据query查询ids
     *
     * @param query 查询条件
     * @return List<Long>
     */
    <#if cfg.isDatabaseMasterSlaveMode>
    @Slave
    </#if>
    List<Long> idsByQuery(@Param("query") ${queryName} query);

    /**
     * 根据查询条件统计数量
     *
     * @param query 查询条件
     * @return Integer
     */
    <#if cfg.isDatabaseMasterSlaveMode>
    @Slave
    </#if>
    Integer countByQuery(@Param("query") ${queryName} query);

    /**
     * 根据id查询详情
     *
     * @param id 数据Id
     * @return ${entityName}
     */
    <#if cfg.isDatabaseMasterSlaveMode>
    @Slave
    </#if>
    @Override
    ${entityName} selectById(@Param("id") Serializable id);

    /**
     * 根据id查询详情
     *
     * @param id 数据Id
     * @return ${voName}
     */
    <#if cfg.isDatabaseMasterSlaveMode>
    @Slave
    </#if>
    ${voName} findDetail(@Param("id") <#if idType=='ID_WORKER_STR'>String<#elseif idType=='ASSIGN_ID'>Long<#else>Long</#if> id);
}
</#if>
