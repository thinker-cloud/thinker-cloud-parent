package ${package.Mapper};

import ${superMapperClassPackage};
import com.baomidou.mybatisplus.core.metadata.IPage;
import ${voPackage}.${voName};
import ${entityPackage}.${entityName};
import ${queryPackage}.${queryName};
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
    List<${voName}> page(@Param("page") IPage<${voName}> page, @Param("query") ${queryName} query);

    /**
     * 根据query查询列表
     *
     * @param query 查询条件
     * @return 列表
     */
    List<${voName}> list(@Param("query") ${queryName} query);

    /**
     * 根据查询条件统计数量
     *
     * @param query 查询条件
     * @return Integer
     */
    Integer countByQuery(@Param("query") ${queryName} query);

    /**
     * 根据id查询详情
     *
     * @param id 数据Id
     * @return ${voName}
     */
    ${voName} findDetail(@Param("id") <#if idType=='ID_WORKER_STR'>String<#elseif idType=='ASSIGN_ID'>Long<#else>Long</#if> id);
}
</#if>
