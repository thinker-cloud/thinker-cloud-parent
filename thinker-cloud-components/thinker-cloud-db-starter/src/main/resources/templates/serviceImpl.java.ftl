package ${package.ServiceImpl};

import com.baomidou.mybatisplus.core.metadata.IPage;
import ${superServiceImplClassPackage};
import org.springframework.stereotype.Service;
import ${voPackage}.${voName};
import ${dtoPackage}.${dtoName};
import ${entityPackage}.${entityName};
import ${package.Mapper}.${table.mapperName};
import ${package.Service}.${table.serviceName};
import ${queryPackage}.${queryName};
import ${converterPackage}.${converterName};
import com.thinker.cloud.common.exception.FailException;

import java.util.List;
import java.util.Optional;

/**
 * ${table.comment!} 服务实现类
 *
 * @author ${author}
 * @since ${date}
 */
@Service
<#if kotlin>
open class ${table.serviceImplName} : ${superServiceImplClass}<${table.mapperName}, ${entityName}>(), ${table.serviceName} {

}
<#else>
public class ${table.serviceImplName} extends ${superServiceImplClass}<${table.mapperName}, ${entityName}> implements ${table.serviceName} {

    @Override
    public List<${voName}> page(IPage<${voName}> page, ${queryName} query) {
        return baseMapper.page(page, query);
    }

    @Override
    public List<${voName}> list(${queryName} query) {
        return baseMapper.list(query);
    }

    @Override
    public List<Long> idsByQuery(${queryName} query) {
        return baseMapper.idsByQuery(query);
    }

    @Override
    public Integer countByQuery(${queryName} query) {
        return baseMapper.countByQuery(query);
    }

    @Override
    public ${voName} findDetail(<#if idType=='ID_WORKER_STR'>String<#elseif idType=='ASSIGN_ID'>Long<#else>Long</#if> id) {
        return baseMapper.findDetail(id);
    }

    @Override
    public Boolean saveData(${dtoName} dto) {
        // 转换数据并保存
        ${entityName} entity = ${converterName}.INSTANTS.toEntity(dto);
        return super.save(entity);
    }

    @Override
    public Boolean modifyById(${dtoName} dto) {
        // 检查数据是否存在
        ${entityName} oldEntity = baseMapper.selectById(dto.getId());
        Optional.ofNullable(oldEntity).orElseThrow(() -> new FailException("操作失败，数据不存在"));

        // 转换数据并更新
        ${entityName} entity = ${converterName}.INSTANTS.toEntity(dto);
        return super.updateById(entity);
    }

    @Override
    public Boolean removeById(<#if idType=='ID_WORKER_STR'>String<#elseif idType=='ASSIGN_ID'>Long<#else>Long</#if> id) {
        // 检查数据是否存在
        ${entityName} oldEntity = baseMapper.selectById(id);
        Optional.ofNullable(oldEntity).orElseThrow(() -> new FailException("操作失败，数据不存在"));

        <#-- [3.5.0] 使用实体删除才能触发自动填充字段策略，记录操作人、更新时间等信息 -->
        // 删除数据
        return super.removeById(id);
    }
}
</#if>
