package com.thinker.cloud.db.generator;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.config.converts.TypeConverts;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.querys.DbQueryRegistry;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;
import com.google.common.collect.Maps;
import com.thinker.cloud.core.model.query.PageQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * 业务代码生成器
 *
 * @author admin
 */
@Slf4j
public class CodeGenerator {
    /**
     * JDBC相关配置
     */
    private static final String URL = "jdbc:mysql://localhost:13306/ycyd_assets?allowMultiQueries=true&useUnicode=true&characterEncoding=UTF-8&useSSL=false";
    private static final String USER_NAME = "root";
    private static final String PASSWORD = "123456";

    /**
     * 数据库类型
     */
    private static final DbType DB_TYPE = DbType.MYSQL;

    /**
     * 生成在哪个包下
     */
    private static final String PARENT_PACKAGE_NAME = "com.thinker.cloud.account";

    /**
     * 代码生成者
     */
    private static final String AUTHOR = "admin";

    /**
     * 是否初始化生成,设为false则不会覆盖除entity以外的代码
     */
    private static final boolean IS_INIT_CODE_GENERATOR = true;

    /**
     * 是否生成所有默认查询条件
     */
    private static final boolean IS_GENERATE_ALL_DEFAULT_CONDITION = true;

    /**
     * 启动入口
     *
     * @param args args
     */
    public static void main(String[] args) {
        GlobalConfig globalConfig = globalConfig();

        new AutoGenerator(dataSourceConfig())
                // 全局配置
                .global(globalConfig)
                // 包配置
                .packageInfo(packageConfig())
                // 策略配置
                .strategy(strategyConfig())
                // 自定义配置
                .injection(injectionConfig(globalConfig))
                // 配置模板
                .template(initTemplateConfig())
                // 使用Freemarker模板引擎生成
                .execute(new EnhanceFreemarkerTemplateEngine());
    }

    /**
     * 数据源配置
     *
     * @return DataSourceConfig
     */
    private static DataSourceConfig dataSourceConfig() {
        DbQueryRegistry registry = new DbQueryRegistry();
        return new DataSourceConfig
                .Builder(URL, USER_NAME, PASSWORD)
                .dbQuery(registry.getDbQuery(DB_TYPE))
                .typeConvert(TypeConverts.getTypeConvert(DB_TYPE))
                .build();
    }

    /**
     * 全局配置
     *
     * @return GlobalConfig
     */
    private static GlobalConfig globalConfig() {
        return new GlobalConfig.Builder()
                // 指定输出目录
                .outputDir(getCurrentProjectPath() + "/src/main/java")
                // 作者名
                .author(AUTHOR)
                // 开启 springdoc 模式
//                .enableSpringdoc()
                // 注释日期
                .commentDate(DatePattern.NORM_DATETIME_PATTERN)
                // 禁止打开目录
                .disableOpenDir()
                .build();
    }

    /**
     * 包配置
     *
     * @return PackageConfig
     */
    private static PackageConfig packageConfig() {
        return new PackageConfig.Builder()
                .moduleName(scanner("模块名"))
                .parent(PARENT_PACKAGE_NAME)
                .xml("mapper")
                .build();
    }

    /**
     * 策略配置
     *
     * @return StrategyConfig
     */
    private static StrategyConfig strategyConfig() {
        return new StrategyConfig.Builder()
                .disableSqlFilter()
                .addInclude(scanner("表名，多个英文逗号分割").split(","))
                .addTablePrefix("tb_")

                // entity文件策略
                .entityBuilder()
                // 开启 lombok 模型
                .enableLombok()
                // 允许文件覆盖
                .enableFileOverride()
                // 全局主键类型
                .idType(IdType.ASSIGN_ID)
                .naming(NamingStrategy.underline_to_camel)
                .columnNaming(NamingStrategy.underline_to_camel)
                // 设置父类
//                .superClass(BaseEntity.class)
                // 添加父类公共字段(这些字段将不在实体中生成)
//                .addSuperEntityColumns("create_time", "update_time")

                // 逻辑删除字段名(数据库)
                .logicDeleteColumnName("deleted")
                // 乐观锁字段名(数据库)
                .versionColumnName("version")
                // 添加表字段填充
                .addTableFills(
                        new Column("create_by", FieldFill.INSERT),
                        new Column("create_time", FieldFill.INSERT),
                        new Column("update_by", FieldFill.INSERT_UPDATE),
                        new Column("update_time", FieldFill.INSERT_UPDATE)
                )

                // controller文件策略
                .controllerBuilder()
                .enableFileOverride()
                .enableRestStyle()
                .enableHyphenStyle()

                // service文件策略
                .serviceBuilder()
                .enableFileOverride()
                .build();
    }

    /**
     * 自定义生成配置
     *
     * @param gc globalConfig
     * @return InjectionConfig
     */
    private static InjectionConfig injectionConfig(GlobalConfig gc) {
        List<CustomFile> customFiles = new ArrayList<>();

        // 自定义配置会被优先输出
        Map<String, Object> customMap = Maps.newHashMap();
        customMap.put("isGenerateAllDefaultCondition", IS_GENERATE_ALL_DEFAULT_CONDITION);

        if (IS_INIT_CODE_GENERATOR) {
            String outPutDir = gc.getOutputDir() + "/";

            // entity
            String entityPackage = PARENT_PACKAGE_NAME + ".model.entity";
            customFiles.add(new CustomFile.Builder()
                    .templatePath("/templates/entity.java.ftl")
                    .packageName(entityPackage)
                    .filePath(outPutDir)
                    .formatNameFunction(tableInfo -> tableInfo.getEntityName() + "Entity")
                    .fileName(".java")
                    .build());
            customMap.put("entityPackage", entityPackage);

            // Query
            String queryPackage = PARENT_PACKAGE_NAME + ".model.query";
            customFiles.add(new CustomFile.Builder()
                    .templatePath("/templates/query.java.ftl")
                    .packageName(queryPackage)
                    .filePath(outPutDir)
                    .formatNameFunction(tableInfo -> tableInfo.getEntityName() + "Query")
                    .fileName(".java")
                    .build());
            customMap.put("querySuperClass", PageQuery.class);
            customMap.put("queryPackage", queryPackage);

            // DTO
            String dtoPackage = PARENT_PACKAGE_NAME + ".model.dto";
            customFiles.add(new CustomFile.Builder()
                    .templatePath("/templates/DTO.java.ftl")
                    .packageName(dtoPackage)
                    .filePath(outPutDir)
                    .formatNameFunction(tableInfo -> tableInfo.getEntityName() + "DTO")
                    .fileName(".java")
                    .build());
            customMap.put("dtoPackage", dtoPackage);

            // VO
            String voPackage = PARENT_PACKAGE_NAME + ".model.vo";
            customFiles.add(new CustomFile.Builder()
                    .templatePath("/templates/VO.java.ftl")
                    .packageName(voPackage)
                    .filePath(outPutDir)
                    .formatNameFunction(tableInfo -> tableInfo.getEntityName() + "VO")
                    .fileName(".java")
                    .build());
            customMap.put("voPackage", voPackage);

            // converter
            String converterPackage = PARENT_PACKAGE_NAME + ".converter";
            customFiles.add(new CustomFile.Builder()
                    .templatePath("/templates/converter.java.ftl")
                    .packageName(converterPackage)
                    .filePath(outPutDir)
                    .formatNameFunction(tableInfo -> tableInfo.getEntityName() + "Converter")
                    .fileName(".java")
                    .build());
            customMap.put("converterPackage", converterPackage);
        }

        // 自定义属性注入
        return new InjectionConfig.Builder()
                .customMap(customMap)
                .customFile(customFiles)
                .build();
    }

    /**
     * 根据自己的需要，修改哪些包下面的 要覆盖还是不覆盖
     *
     * @return TemplateConfig
     */
    private static TemplateConfig initTemplateConfig() {
        // 配置自定义输出模板
        // 指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        TemplateConfig templateConfig = new TemplateConfig.Builder()
                .disable(TemplateType.ENTITY)
                .controller("/templates/controller.java")
                .service("/templates/service.java")
                .serviceImpl("/templates/serviceImpl.java")
                .mapper("/templates/mapper.java")
                .xml("/templates/mapper.xml")
                .build();

        // 不是第一次生成，则不生成以下类
        if (!IS_INIT_CODE_GENERATOR) {
            templateConfig.disable(TemplateType.CONTROLLER);
            templateConfig.disable(TemplateType.SERVICE);
            templateConfig.disable(TemplateType.SERVICE_IMPL);
            templateConfig.disable(TemplateType.MAPPER);
            templateConfig.disable(TemplateType.XML);
        }
        return templateConfig;
    }

    /**
     * 模板增强，自定义[DTO\VO等]模版
     */
    public static class EnhanceFreemarkerTemplateEngine extends FreemarkerTemplateEngine {

        @NotNull
        @Override
        public Map<String, Object> getObjectMap(@NotNull ConfigBuilder config, @NotNull TableInfo tableInfo) {
            // 获取实体类名字
            String entityName = tableInfo.getEntityName();

            // 定义类名
            Map<String, Object> objectMap = super.getObjectMap(config, tableInfo);
            objectMap.put("queryName", entityName + "Query");
            objectMap.put("dtoName", entityName + "DTO");
            objectMap.put("entityName", entityName + "Entity");
            objectMap.put("voName", entityName + "VO");
            objectMap.put("converterName", entityName + "Converter");
            return objectMap;
        }
    }

    /**
     * 获取当前项目路径
     *
     * @return String
     */
    private static String getCurrentProjectPath() {
        String userDir = System.getProperty("user.dir");
        String modelPath = System.getProperty("java.class.path").replaceFirst("[/|\\\\]target[/|\\\\].*$", "");
        if (userDir.equals(modelPath)) {
            return userDir;
        }
        int index = modelPath.lastIndexOf(";");
        return index == -1 ? modelPath : modelPath.substring(index + 1);
    }

    /**
     * 读取控制台内容
     *
     * @param tip tip
     * @return String
     */
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入" + tip + "：");
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotEmpty(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }
}
