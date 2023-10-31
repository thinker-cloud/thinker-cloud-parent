package ${dtoPackage};

<#if springdoc>
import io.swagger.v3.oas.annotations.media.Schema;
<#elseif swagger>
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
</#if>
<#if entityLombokModel>
import lombok.Data;
import lombok.experimental.Accessors;
</#if>

import java.io.Serial;
import java.io.Serializable;
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
/**
 * ${table.comment!}
 *
 * @author ${author}
 * @since ${date}
 */
<#if entityLombokModel>
@Data
@Accessors(chain = true)
</#if>
<#if springdoc>
@Schema(name = "${dtoName}", description = "${table.comment!}")
<#elseif swagger>
@ApiModel(value = "${dtoName}", description = "${table.comment!}")
</#if>
public class ${dtoName} implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
<#-- ----------  BEGIN 字段循环遍历  ---------->
<#list table.fields as field>
    <#if !field.logicDeleteField && !field.versionField && !field.fill??>
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
    </#if>
</#list>
<#------------  END 字段循环遍历  ---------->
}
