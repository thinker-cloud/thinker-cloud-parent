package ${voPackage};

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
import ${entityPackage}.${entityName};

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
@Schema(name = "${voName}", description = "${table.comment}!")
<#elseif swagger>
@ApiModel(value = "${voName}", description = "${table.comment!}")
</#if>
public class ${voName} extends ${entityName} {

}
