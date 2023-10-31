<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${package.Mapper}.${table.mapperName}">
<#if enableCache>
    <!-- 开启二级缓存 -->
    <cache type="org.mybatis.caches.ehcache.LoggingEhcache"/>
</#if>
<#if baseResultMap>
    <!-- 通用查询映射结果 -->
    <resultMap id="BaseResultMap" type="${entityPackage}.${entityName}">
<#list table.fields as field>
<#if field.keyFlag><#--生成主键排在第一位-->
        <id column="${field.name}" property="${field.propertyName}" />
</#if>
</#list>
<#list table.commonFields as field><#--生成公共字段 -->
    <result column="${field.name}" property="${field.propertyName}" />
</#list>
<#list table.fields as field>
<#if !field.keyFlag><#--生成普通字段 -->
        <result column="${field.name}" property="${field.propertyName}" />
</#if>
</#list>
    </resultMap>
</#if>
<#if baseColumnList>
    <!-- 通用查询结果列 -->
    <sql id="Base_Column_List">
<#list table.commonFields as field>
        ${field.name},
</#list>
        ${table.fieldNames}
    </sql>
</#if>
    <resultMap id="entity" type="${entityPackage}.${entityName}"/>
    <resultMap id="vo" extends="entity" type="${voPackage}.${voName}"/>
    <sql id="condition">
        <if test="query != null">
        <#if cfg.isGenerateAllDefaultCondition>
        <#list table.fields as field>
            <#if !field.logicDeleteField>
                <#if field.propertyType=='String' && field.propertyName!='id'>
            <if test="query.${field.propertyName} != null and query.${field.propertyName} != ''">
                and base.${field.name} like concat('%',<#noparse>#</#noparse>{query.${field.propertyName}},'%')
                <#else>
            <if test="query.${field.propertyName} != null">
                and base.${field.name} = <#noparse>#</#noparse>{query.${field.propertyName}}
                </#if>
            </if>
                <#if field.propertyType=='Date' || field.propertyType=='LocalDateTime'>
            <if test="query.start${field.propertyName?cap_first} != null">
                and base.${field.name} >= <#noparse>#</#noparse>{query.start${field.propertyName?cap_first}}
            </if>
            <if test="query.end${field.propertyName?cap_first} != null">
                and base.${field.name} &lt;= <#noparse>#</#noparse>{query.end${field.propertyName?cap_first}}
            </if>
                </#if>
            <#else>
            <if test="query.isIncludeDelete == null or !query.isIncludeDelete">
                and base.${field.name} = false
            </if>
            </#if>
        </#list>
        </#if>
        </if>
    </sql>

    <select id="page" resultMap="vo">
        select base.*
        from ${table.name} as base
        <where>
            <include refid="condition"/>
        </where>
        <choose>
            <when test="query.orderField != null and query.orderField != ''">
                order by ${r'${query.orderField}'} ${r'${query.order}'}
            </when>
            <otherwise>
                order by base.create_time desc
            </otherwise>
        </choose>
    </select>

    <select id="list" resultMap="vo">
        select base.*
        from ${table.name} as base
        <where>
            <include refid="condition"/>
        </where>
        <choose>
            <when test="query.orderField != null and query.orderField != ''">
                order by ${r'${query.orderField}'} ${r'${query.order}'}
            </when>
            <otherwise>
                order by base.create_time desc
            </otherwise>
        </choose>
    </select>

    <select id="countByQuery" resultType="java.lang.Integer">
        select count(1)
        from ${table.name} as base
        <where>
            <include refid="condition"/>
        </where>
    </select>

    <select id="findDetail" resultMap="vo">
        select base.*
        from ${table.name} as base
        where base.id = ${r"#"}{id}
        <#list table.fields as field>
        <#if field.logicDeleteField>
        and base.${field.name} = false
        </#if>
        </#list>
    </select>
</mapper>
