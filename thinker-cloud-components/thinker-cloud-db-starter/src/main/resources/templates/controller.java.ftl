package ${package.Controller};

import com.baomidou.mybatisplus.core.metadata.IPage;
<#if springdoc>
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
<#elseif swagger>
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
</#if>
<#if entityLombokModel>
import lombok.AllArgsConstructor;
</#if>
import org.springframework.web.bind.annotation.*;
import ${package.Service}.${table.serviceName};
import ${dtoPackage}.${dtoName};
import ${queryPackage}.${queryName};
import ${voPackage}.${voName};

<#if restControllerStyle>
import org.springframework.web.bind.annotation.RestController;
<#else>
import org.springframework.stereotype.Controller;
</#if>
<#if superControllerClassPackage??>
import ${superControllerClassPackage};
</#if>
import com.thinker.cloud.core.model.Result;
import com.thinker.cloud.core.model.vo.PageVO;

import javax.validation.Valid;
import java.util.List;

/**
 * ${table.comment!} 前端控制器
 *
 * @author ${author}
 * @since ${date}
 */
<#if springdoc>
@Tag(name = "${table.controllerName}", description = "${table.comment!}")
<#elseif swagger>
@Api(tags = "${table.controllerName}", description = "${table.comment!}")
</#if>
<#if restControllerStyle>
@RestController
<#else>
@Controller
</#if>
@AllArgsConstructor
@RequestMapping("<#if controllerMappingHyphenStyle??>${controllerMappingHyphen}<#else>${table.entityPath}</#if>")
<#if kotlin>
class ${table.controllerName}<#if superControllerClass??> : ${superControllerClass}()</#if>
<#else>
<#if superControllerClass??>
public class ${table.controllerName} extends ${superControllerClass} {
<#else>
public class ${table.controllerName} {
</#if>

    private final ${table.serviceName} ${table.serviceName?substring(1)?uncap_first};

<#if !springdoc && !swagger>
    /**
     * 分页列表
     *
     * @return Result<PageVO<${voName}>> page data list
     */
</#if>
    @PostMapping(value = "page")
<#if springdoc>
    @Operation(summary = "分页列表", description = "传入page与limit可分页查询")
<#elseif swagger>
    @ApiOperation(value = "分页列表", notes = "传入page与limit可分页查询")
</#if>
    public Result<PageVO<${voName}>> page(@RequestBody ${queryName} query) {
        IPage<${voName}> page = query.generatePage();
        ${table.serviceName?substring(1)?uncap_first}.page(page, query);
        return Result.buildSuccess(new PageVO<>(page));
    }

<#if !springdoc && !swagger>
    /**
     * 不分页列表
     *
     * @return Result<List<${voName}>>  data list
     */
</#if>
    @PostMapping(value = "list")
<#if springdoc>
    @Operation(summary = "不分页列表", description = "所有数据列表查询")
<#elseif swagger>
    @ApiOperation(value = "不分页列表", notes = "所有数据列表查询")
</#if>
    public Result<List<${voName}>> list(@RequestBody ${queryName} query) {
        return Result.buildSuccess(${table.serviceName?substring(1)?uncap_first}.list(query));
    }

<#if !springdoc && !swagger>
    /**
     * 根据id查询
     *
     * @return Result<${voName}>  ${voName}
     */
</#if>
    @GetMapping(value = "{id}")
<#if springdoc>
    @Operation(summary = "根据id查询")
<#elseif swagger>
    @ApiOperation(value = "根据id查询")
</#if>
    public Result<${voName}> detail(@PathVariable("id") <#if idType=='ID_WORKER_STR'>String<#elseif idType=='ASSIGN_ID'>Long<#else>Long</#if> id) {
        return Result.buildSuccess(${table.serviceName?substring(1)?uncap_first}.findDetail(id));
    }

<#if !springdoc && !swagger>
    /**
     * 新增数据
     *
     * @return Result<Boolean>  Boolean
     */
</#if>
    @PostMapping
<#if springdoc>
    @Operation(summary = "新增数据")
<#elseif swagger>
    @ApiOperation(value = "新增数据")
</#if>
    public Result<Boolean> saveData(@RequestBody @Valid ${dtoName} dto) {
        return Result.buildSuccess(${table.serviceName?substring(1)?uncap_first}.saveData(dto));
    }

<#if !springdoc && !swagger>
    /**
     * 根据id修改数据
     *
     * @return Result<Boolean>  Boolean
     */
</#if>
    @PutMapping
<#if springdoc>
    @Operation(summary = "修改数据", description = "根据id修改数据")
<#elseif swagger>
    @ApiOperation(value = "修改数据", notes = "根据id修改数据")
</#if>
    public Result<Boolean> modifyById(@RequestBody @Valid ${dtoName} dto) {
        return Result.buildSuccess(${table.serviceName?substring(1)?uncap_first}.modifyById(dto));
    }

<#if !springdoc && !swagger>
    /**
     * 根据id删除数据
     *
     * @return Result<Boolean>  Boolean
     */
</#if>
    @DeleteMapping(value = "{id}")
<#if springdoc>
    @Operation(summary = "删除数据", description = "根据id删除数据")
<#elseif swagger>
    @ApiOperation(value = "删除数据", notes = "根据id删除数据")
</#if>
    public Result<Boolean> removeById(@PathVariable("id") <#if idType=='ID_WORKER_STR'>String<#elseif idType=='ASSIGN_ID'>Long<#else>Long</#if> id) {
        return Result.buildSuccess(${table.serviceName?substring(1)?uncap_first}.removeById(id));
    }
}
</#if>
