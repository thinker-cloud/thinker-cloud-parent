package ${converterPackage};

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ${voPackage}.${voName};
import ${dtoPackage}.${dtoName};
import ${entityPackage}.${entityName};

import java.util.List;

/**
 * ${table.comment!} 对象转换器
 *
 * @author ${author}
 * @since ${date}
 **/
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ${converterName} {

    ${converterName} INSTANTS = Mappers.getMapper(${converterName}.class);

    /**
     * dto to Entity
     *
     * @param dto dto
     * @return ${entityName}
     */
    ${entityName} toEntity(${dtoName} dto);

    /**
     * entity to VO
     *
     * @param entity entity
     * @return ${voName}
     */
    ${voName} toVO(${entityName} entity);

    /**
     * entity to DTO
     *
     * @param entity entity
     * @return ${entityName}
     */
    ${dtoName} toDTO(${entityName} entity);

    /**
     * list DTO to entity
     *
     * @param list list
     * @return List<${entityName}>
     */
    List<${entityName}> listToEntity(List<${dtoName}> list);

    /**
     * list entity to VO
     *
     * @param list list
     * @return List<${voName}>
     */
    List<${voName}> listToVO(List<${entityName}> list);

    /**
     * list entity to DTO
     *
     * @param list list
     * @return List<${entityName}>
     */
    List<${dtoName}> listToDTO(List<${entityName}> list);
}
