package ${queryPackage};

<#if springdoc>
import io.swagger.v3.oas.annotations.media.Schema;
<#elseif swagger>
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
</#if>
<#if entityLombokModel>
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
</#if>
import com.thinker.cloud.core.model.query.PageQuery;
<#if querySuperClass??>
import ${querySuperClass.canonicalName};
</#if>
<#list table.fields as field>
<#if field.propertyType=='Date'>
import java.util.Date;
<#elseif field.propertyType=='LocalDateTime'>
import java.time.LocalDateTime;
</#if>
<#if field.propertyType=='BigDecimal'>
import java.math.BigDecimal;
</#if>
</#list>
<#list table.importPackages as pkg>
import ${pkg};
</#list>

/**
 * ${table.comment!}
 *
 * @author ${author}
 * @since ${date}
 */
<#if entityLombokModel>
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
</#if>
<#if springdoc>
@Schema(name = "${queryName}", description = "${table.comment}!")
<#elseif swagger>
@ApiModel(value = "${queryName}", description = "${table.comment!}")
</#if>
<#if querySuperClass??>
public class ${queryName} extends ${querySuperClass.simpleName} {
<#else>
public class ${queryName} extends PageQuery {
</#if>
<#-- ----------  BEGIN 字段循环遍历  ---------->
<#list table.fields as field>
    <#if !field.logicDeleteField && !field.versionField>
        <#if field.keyFlag>
            <#assign keyPropertyName="${field.propertyName}"/>
        </#if>

        <#if field.comment!?length gt 0>
            <#if springdoc>
    @Schema(description = "${field.comment}")
            <#elseif swagger>
    @ApiModelProperty(value = "${field.comment}")
            <#else>
    /**
     * ${field.comment}
     */
            </#if>
        </#if>
    private ${field.propertyType} ${field.propertyName};
    <#else>

        <#if field.comment!?length gt 0>
            <#if springdoc>
    @Schema(description = "是否包含删除数据")
            <#elseif swagger>
    @ApiModelProperty(value = "是否包含删除数据")
            <#else>
    /**
     * 是否包含删除数据
     */
            </#if>
        </#if>
    private Boolean isIncludeDelete;
    </#if>
    <#if field.propertyType=='Date' || field.propertyType=='LocalDateTime'>

        <#if field.comment!?length gt 0>
            <#if springdoc>
    @Schema(description = "筛选起始:${field.comment!''}")
            <#elseif swagger>
    @ApiModelProperty(value = "筛选起始:${field.comment!''}")
            <#else>
    /**
     * 筛选起始:${field.comment!''}
     */
            </#if>
        </#if>
    private ${field.propertyType} start${field.propertyName?cap_first};

        <#if field.comment!?length gt 0>
            <#if springdoc>
    @Schema(description = "筛选结束:${field.comment!''}")
            <#elseif swagger>
    @ApiModelProperty(value = "筛选结束:${field.comment!''}")
            <#else>
    /**
     * 筛选结束:${field.comment!''}
     */
            </#if>
        </#if>
    private ${field.propertyType} end${field.propertyName?cap_first};
    </#if>
</#list>
<#------------  END 字段循环遍历  ---------->
}
