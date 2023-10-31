package com.thinker.cloud.core.utils.excel;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;
import cn.afterturn.easypoi.handler.inter.IWriter;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinker.cloud.core.enums.ResponseCode;
import com.thinker.cloud.core.excel.BaseVerifyHandler;
import com.thinker.cloud.core.excel.ExcelExportTask;
import com.thinker.cloud.core.exception.FailException;
import com.thinker.cloud.core.utils.MyJsonUtil;
import com.thinker.cloud.core.utils.thread.BatchTaskThreadPool;
import com.thinker.cloud.core.utils.thread.BatchTaskUtil;
import com.thinker.cloud.tools.generator.IDGenerator;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.LongFunction;
import java.util.stream.Collectors;

/**
 * excel 工具类
 *
 * @author admin
 **/
@Slf4j
@UtilityClass
public class ExcelUtil {

    /**
     * 下载excel模板
     *
     * @param response     response
     * @param templateName 模板名称
     */
    @SneakyThrows
    public static void downLoadTemplate(HttpServletResponse response, String templateName) {
        try {
            String path = "/statics/excelTemplate/" + templateName;
            @Cleanup InputStream inputStream = ExcelUtil.class.getResourceAsStream(path);
            if (Objects.isNull(inputStream)) {
                throw new FailException("文件不存在");
            }
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=import-template.xlsx");
            @Cleanup OutputStream outputStream = response.getOutputStream();
            int len;
            byte[] data = new byte[1024];
            // 读取模板文件
            while ((len = inputStream.read(data)) != -1) {
                outputStream.write(data, 0, len);
            }
        } catch (FileNotFoundException e) {
            throw new FailException("模板文件不存在");
        } catch (Exception e) {
            log.error("下载模板文件失败，ex={}", e.getMessage(), e);
            throw new FailException("下载模板文件失败");
        }
    }

    /**
     * 下载zip文件
     *
     * @param zipFilePath zip文件地址
     * @param response    response
     */
    public static void downloadZipFile(String zipFilePath, HttpServletResponse response) {
        File file = FileUtil.file(zipFilePath);
        downloadZipFile(file, response);
    }

    /**
     * 下载zip文件
     *
     * @param zipFile  zip文件
     * @param response response
     */
    public static void downloadZipFile(File zipFile, HttpServletResponse response) {
        try (FileInputStream inputStream = new FileInputStream(zipFile)) {
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType("application/octet-stream;charset=utf-8");
            String filename = URLEncoder.encode(zipFile.getName(), StandardCharsets.UTF_8.name());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename + ".zip");
            @Cleanup OutputStream outputStream = response.getOutputStream();
            int len;
            byte[] data = new byte[1024];
            // 读取模板文件
            while ((len = inputStream.read(data)) != -1) {
                outputStream.write(data, 0, len);
            }
        } catch (FileNotFoundException e) {
            throw FailException.of("文件不存在");
        } catch (Exception e) {
            log.error("下载zip文件失败，ex={}", e.getMessage(), e);
            throw FailException.of("下载zip文件失败");
        }
    }

    /**
     * 导出 Excel
     *
     * @param aClass   返回对象的class
     * @param list     导出的数据列表
     * @param <T>      返回对象类型
     * @param fileName 导出文件名称
     * @param response response
     */
    @SneakyThrows
    public static <T> void exportExcel(Class<T> aClass, List<T> list, String fileName, HttpServletResponse response) {
        ExportParams exportParams = new ExportParams();
        exportParams.setType(ExcelType.XSSF);
        @Cleanup Workbook workbook = ExcelExportUtil.exportExcel(exportParams, aClass, list);
        exportExcel(fileName, workbook, response);
    }

    /**
     * 导出 Excel
     *
     * @param fileName 导出模板文件名称
     * @param workbook 数据流
     * @param response response
     */
    @SneakyThrows
    public static void exportExcel(String fileName, Workbook workbook, HttpServletResponse response) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        String filename = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment;filename=" + filename + ".xlsx");
        @Cleanup OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
    }

    /**
     * 将数据导出到Excel 大数据导出
     *
     * @param total    导出数据总条数
     * @param limit    单次查询数据库条数
     * @param supplier 导出数据获取过程
     * @param fileName 导出文件名称
     * @param aClass   导出数据对象Class
     * @param <T>      导出数据对象类型
     * @param response response
     */
    public static <T> void exportBigExcel(long total, long limit, LongFunction<List<T>> supplier
            , String fileName, Class<T> aClass, HttpServletResponse response) {
        ExportParams exportParams = new ExportParams();
        exportParams.setType(ExcelType.XSSF);
        @Cleanup IWriter<Workbook> writer = ExcelExportUtil.exportBigExcel(exportParams, aClass);

        // 分批次查询并写入Excel
        BatchTaskUtil.batchQuery(total, limit, supplier, writer::write);

        // 导出Excel
        exportExcel(fileName, writer.get(), response);
    }

    /**
     * 将数据导出到多个Excel 导出文件数量超过1个则自动打包成zip下载
     *
     * @param total       导出数据总条数
     * @param limit       单次查询数据库条数
     * @param supplier    导出数据获取过程
     * @param excelMaxNum 单个Excel文件存储最大条数
     * @param fileName    导出文件名称
     * @param aClass      导出数据对象Class
     * @param <T>         导出数据对象类型
     * @param response    response
     */
    public static <T> void exportMultipleExcel(long total, long limit, LongFunction<List<T>> supplier
            , Integer excelMaxNum, String fileName, Class<T> aClass, HttpServletResponse response) {
        // 计算导出任务
        List<Callable<Boolean>> excelExportTasks = genExportExcelTasks(total, limit, supplier
                , excelMaxNum, fileName, aClass, "");

        // 如果导出任务只有一个，则直接导出
        if (excelExportTasks.isEmpty() || excelExportTasks.size() == 1) {
            exportBigExcel(total, limit, supplier, fileName, aClass, response);
            return;
        }

        // 导出任务大于1个，则将数据导出到zip压缩文件
        File zipFile = exportMultipleExcelToZip(total, limit, supplier, excelMaxNum, fileName, aClass);
        downloadZipFile(zipFile, response);
    }

    /**
     * 将数据导出到Excel并压缩成zip文件
     *
     * @param total       导出数据总条数
     * @param limit       单次查询数据库条数
     * @param supplier    导出数据获取过程
     * @param excelMaxNum 单个Excel文件存储最大条数
     * @param fileName    导出文件名称
     * @param aClass      导出数据对象Class
     * @param <T>         导出数据对象类型
     * @return File zip文件
     */
    public static <T> File exportMultipleExcelToZip(long total, long limit, LongFunction<List<T>> supplier
            , Integer excelMaxNum, String fileName, Class<T> aClass) {
        // 导出临时文件存放目录路径
        File file = FileUtil.mkdir(File.separator + "excel_export" +
                File.separator + "temp" + File.separator + IDGenerator.SNOW_FLAKE.generate());

        // 生成导出任务
        List<Callable<Boolean>> excelExportTasks = genExportExcelTasks(total, limit, supplier
                , excelMaxNum, fileName, aClass, file.getPath());

        try {
            // 批量生成导出文件
            List<Boolean> allTask = BatchTaskThreadPool.invokeAllTask(excelExportTasks, 10);
            if (!allTask.stream().allMatch(Boolean::booleanValue)) {
                throw FailException.of("导出文件失败");
            }

            // 将临时文件目录下的文件打成压缩包
            String zipPath = file.getPath() + ".zip";

            // 返回压缩文件
            return ZipUtil.zip(file.getPath(), zipPath);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw FailException.of("导出文件失败");
        } finally {
            // 删除临时文件目录
            FileUtil.del(file.getPath());
        }
    }

    /**
     * 根据模板导出 Excel
     *
     * @param templateName 导出模板文件名称
     * @param dataMap      导出的数据映射Map
     * @param response     response
     */
    @SneakyThrows
    public static void exportExcelByTemplate(String templateName, Map<String, Object> dataMap, HttpServletResponse response) {
        try {
            String path = "statics/excelTemplate/" + templateName;
            TemplateExportParams params = new TemplateExportParams(path);
            @Cleanup Workbook workbook = ExcelExportUtil.exportExcel(params, dataMap);
            exportExcel("export-excel", workbook, response);
        } catch (NullPointerException e) {
            throw new FailException("导出失败，模板不存在");
        } catch (Exception e) {
            log.error("导出失败，ex={}", e.getMessage(), e);
            throw new FailException("导出失败");
        }
    }

    /**
     * 根据模板导出 Excel
     *
     * @param templateFileName 导出模板文件名称
     * @param list             导出的数据列表
     * @param <T>              返回对象类型
     * @param response         response
     */
    @SneakyThrows
    public static <T> void exportExcelByTemplate(String templateFileName, List<T> list, HttpServletResponse response) {
        Map<String, Object> dataMap = Maps.newHashMap();
        List<JSONObject> melist = list.stream()
                .map(MyJsonUtil::toJsonObject)
                .collect(Collectors.toList());
        dataMap.put("list", melist);
        exportExcelByTemplate(templateFileName, dataMap, response);
    }

    /**
     * 导入Excel 取表格第一列数据
     *
     * @param file      file
     * @param titleRows 表格标题行数 0：表示没有标题
     * @return List<T>
     */
    @SneakyThrows
    public static List<String> importExcel(MultipartFile file, Integer titleRows) {
        List<String> list = Lists.newArrayList();
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet1 = workbook.getSheetAt(0);
        for (int i = titleRows; i <= sheet1.getLastRowNum(); i++) {
            String value = ExcelRowUtil.getCellValue(sheet1.getRow(i), 0);
            Optional.ofNullable(value).filter(StrUtil::isNotBlank).ifPresent(list::add);
        }
        return list;
    }

    /**
     * 导入Excel
     *
     * @param file      file
     * @param aClass    返回的对象的Class
     * @param titleRows 表格标题行数
     * @param <T>       返回的对象类型
     * @return List<T>
     */
    @SneakyThrows
    public static <T> List<T> importExcel(MultipartFile file, Class<T> aClass, Integer titleRows) {
        InputStream inputStream = file.getInputStream();
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setVerifyHandler(new BaseVerifyHandler<T>());
        return ExcelImportUtil.importExcel(inputStream, aClass, params);
    }

    /**
     * 导入Excel 并且做导入检查
     * 获取行数和错误信息需要继承 ExcelVerifyInfo 类
     *
     * @param file      file
     * @param aClass    返回的对象的Class
     * @param titleRows 表格标题行数
     * @param <T>       返回的对象类型
     * @return List<T>
     */
    @SneakyThrows
    public static <T> List<T> importExcelMore(MultipartFile file, Class<T> aClass, Integer titleRows) {
        InputStream inputStream = file.getInputStream();
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setVerifyHandler(new BaseVerifyHandler<T>());
        ExcelImportResult<T> result = ExcelImportUtil.importExcelMore(inputStream, aClass, params);
        if (!CollectionUtils.isEmpty(result.getFailList())) {
            // 这里导入正常、失败的数据集合都返回，根据业务自己处理
            throw new FailException(result, ResponseCode.SERVER_FAILURE.getCode(), "导入失败");
        }
        return result.getList();
    }

    /**
     * 导入Excel基础版 并且做导入检查
     * 获取行数和错误信息需要继承 ExcelVerifyInfo 类
     *
     * @param file      file
     * @param aClass    返回的对象的Class
     * @param titleRows 表格标题行数
     * @param <T>       返回的对象类型
     * @return ExcelImportResult<T>
     */
    @SneakyThrows
    public static <T> ExcelImportResult<T> importExcelMoreBase(MultipartFile file, Class<T> aClass, Integer titleRows) {
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setVerifyHandler(new BaseVerifyHandler<T>());
        return ExcelImportUtil.importExcelMore(file.getInputStream(), aClass, params);
    }

    /**
     * 生成导出 Excel任务对象
     *
     * @param total       导出数据总条数
     * @param limit       单次查询数据库条数
     * @param supplier    导出数据获取过程
     * @param excelMaxNum 单个Excel文件存储最大条数
     * @param fileName    导出文件名称
     * @param aClass      导出数据对象Class
     * @param filepath    导出文件路径
     * @param <T>         导出数据对象类型
     * @return zip文件地址
     */
    private static <T> List<Callable<Boolean>> genExportExcelTasks(long total, long limit, LongFunction<List<T>> supplier
            , Integer excelMaxNum, String fileName, Class<T> aClass, String filepath) {
        // 根据数据总数计算生成文件数量
        long fileTotal = total / excelMaxNum + (total % excelMaxNum > 0 ? 1 : 0);

        List<Callable<Boolean>> excelExportTasks = Lists.newArrayList();
        for (int i = 1; i <= fileTotal; i++) {
            String filePath = filepath + File.separator + fileName + i + ".xlsx";
            ExcelExportTask<T> excelExportTask = new ExcelExportTask<>(filePath, aClass, supplier);
            excelExportTask.generatePage(excelMaxNum, limit, i);

            // 只用存一个文件
            if (fileTotal == 1) {
                excelExportTasks.add(excelExportTask.setTotal(total));
                return excelExportTasks;
            }

            // 计算最后一个文件实际存放多少数据
            // 少于excel最大存储数的10分之一，则直接放入当前文件中
            long lastFileDataCount = total - ((fileTotal - 1) * excelMaxNum);
            if (i == (fileTotal - 1) && lastFileDataCount < (excelMaxNum / 10)) {
                long fileDataTotal = excelMaxNum + lastFileDataCount;
                excelExportTasks.add(excelExportTask.setTotal(fileDataTotal));
                return excelExportTasks;
            }

            // 最后一个文件
            if (i == fileTotal && lastFileDataCount > 0) {
                excelExportTask.setTotal(lastFileDataCount);
            }

            excelExportTasks.add(excelExportTask);
        }
        return excelExportTasks;
    }
}
