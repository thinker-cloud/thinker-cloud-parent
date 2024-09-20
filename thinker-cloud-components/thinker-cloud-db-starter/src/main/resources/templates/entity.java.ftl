package ${entityPackage};

<#list table.importPackages as pkg>
import ${pkg};
</#list>
<#if springdoc>
import io.swagger.v3.oas.annotations.media.Schema;
<#elseif swagger>
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
</#if>
<#if entityLombokModel>
import lombok.Data;
<#if superEntityClass??>
import lombok.EqualsAndHashCode;
</#if>
import lombok.experimental.Accessors;
</#if>
import java.io.Serial;
import java.io.Serializable;
<#list table.fields as field>
<#if field.propertyType=='Date'>
import java.util.Date;
<#elseif field.propertyType=='LocalTime'>
import java.time.LocalTime;
<#elseif field.propertyType=='LocalDate'>
import java.time.LocalDate;
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
<#if superEntityClass??>
@EqualsAndHashCode(callSuper = true)
</#if>
<#if table.convert>
@TableName(value = "${table.name}")
</#if>
<#if springdoc>
@Schema(name = "${entityName}", description = "${table.comment!}")
<#elseif swagger>
@ApiModel(value = "${entityName}", description = "${table.comment!}")
</#if>
<#if superEntityClass??>
public class ${entityName} extends ${superEntityClass}<#if activeRecord><${entityName}></#if> {
<#elseif activeRecord>
public class ${entityName} extends Model<${entityName}> {
<#else>
public class ${entityName} implements Serializable {
</#if>

<#if entitySerialVersionUID>
    @Serial
    private static final long serialVersionUID = 1L;
</#if>
<#-- ----------  BEGIN 字段循环遍历  ---------->
<#list table.fields as field>
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
    <#if field.keyFlag>
    <#-- 主键 -->
        <#if field.keyIdentityFlag>
    @TableId(value = "${field.name}", type = IdType.AUTO)
        <#elseif idType??>
    @TableId(value = "${field.name}", type = IdType.${idType})
        <#elseif field.convert>
    @TableId("${field.name}")
        </#if>
    <#-- 普通字段 -->
    <#elseif field.fill??>
    <#-- -----   存在字段填充设置   ----->
        <#if field.convert>
    @TableField(value = "${field.name}", fill = FieldFill.${field.fill})
        <#else>
    @TableField(fill = FieldFill.${field.fill})
        </#if>
    <#elseif field.convert>
    @TableField("${field.name}")
    </#if>
<#-- 乐观锁注解 -->
    <#if field.versionField>
    @Version
    </#if>
<#-- 逻辑删除注解 -->
    <#if field.logicDeleteField>
    @TableLogic
    private Boolean ${field.propertyName};
    <#else>
    private ${field.propertyType} ${field.propertyName};
    </#if>
</#list>
<#------------  END 字段循环遍历  ---------->
<#if !entityLombokModel>
    <#list table.fields as field>
        <#if field.propertyType == "boolean">
            <#assign getprefix="is"/>
        <#else>
            <#assign getprefix="get"/>
        </#if>
        public ${field.propertyType} ${getprefix}${field.capitalName}() {
        return ${field.propertyName};
        }

        <#if entityBuilderModel>
            public ${entityName} set${field.capitalName}(${field.propertyType} ${field.propertyName}) {
        <#else>
            public void set${field.capitalName}(${field.propertyType} ${field.propertyName}) {
        </#if>
        this.${field.propertyName} = ${field.propertyName};
        <#if entityBuilderModel>
            return this;
        </#if>
        }
    </#list>
</#if>
<#if entityColumnConstant>
    <#list table.fields as field>
        public static final String ${field.name?upper_case} = "${field.name}";

    </#list>
</#if>
<#if activeRecord>
    @Override
    protected Serializable pkVal() {
    <#if keyPropertyName??>
        return this.${keyPropertyName};
    <#else>
        return null;
    </#if>
    }

</#if>
<#if !entityLombokModel>
    @Override
    public String toString() {
    return "${entityName}{" +
    <#list table.fields as field>
        <#if field_index==0>
            "${field.propertyName}=" + ${field.propertyName} +
        <#else>
            ", ${field.propertyName}=" + ${field.propertyName} +
        </#if>
    </#list>
    "}";
    }
</#if>
}
