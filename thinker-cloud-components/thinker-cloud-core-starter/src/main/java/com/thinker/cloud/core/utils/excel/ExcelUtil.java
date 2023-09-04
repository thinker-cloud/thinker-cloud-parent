package com.thinker.cloud.core.utils.excel;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.thinker.cloud.core.enums.ResponseCode;
import com.thinker.cloud.core.excel.BaseVerifyHandler;
import com.thinker.cloud.core.exception.FailException;
import com.thinker.cloud.core.utils.MyJsonTools;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
            response.setHeader("Content-Disposition", "attachment; filename=import-template.xlsx");
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
    private static void exportExcel(String fileName, Workbook workbook, HttpServletResponse response) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        String filename = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment;filename=" + filename + ".xlsx");
        @Cleanup OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
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
                .map(MyJsonTools::toJsonObject)
                .collect(Collectors.toList());
        dataMap.put("list", melist);
        exportExcelByTemplate(templateFileName, dataMap, response);
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
            throw new FailException(result, ResponseCode.FAILURE.getCode(), "导入失败");
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
     * 生成下拉框
     * firstRow 开始行号(下标0开始)
     * lastRow  结束行号，最大65535
     * firstCol 区域中第一个单元格的列号 (下标0开始)
     * lastCol 区域中最后一个单元格的列号
     * dataArray 下拉内容
     * sheetHidden 影藏的sheet编号（例如1,2,3），多个下拉数据不能使用同一个
     */
    public static void selectList(Workbook workbook, int firstRow, int lastRow, int firstCol, int lastCol, String[] dataArray, int sheetHidden) {
        String hiddenName = "hidden_" + System.currentTimeMillis() + (int) ((Math.random() * 9 + 1) * 100 + (Math.random() * 9 + 1) * 1000);
        Sheet sheet = workbook.getSheetAt(0);
        Sheet hidden = workbook.createSheet(hiddenName);

        for (int i = 0; i < dataArray.length; i++) {
            String name = dataArray[i];
            Row row = hidden.createRow(i);
            Cell cell = row.createCell(0);
            cell.setCellValue(name);
        }

        Name namedCell = workbook.createName();
        namedCell.setNameName(hiddenName);
        namedCell.setRefersToFormula(hiddenName + "!$A$1:$A$" + dataArray.length);

        // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
        DataValidationHelper dataValidationHelper = hidden.getDataValidationHelper();
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        DataValidationConstraint constraint = dataValidationHelper.createFormulaListConstraint(hiddenName);
        DataValidation createValidation = dataValidationHelper.createValidation(constraint, addressList);

        // 处理Excel兼容性问题
        if (createValidation instanceof XSSFDataValidation) {
            createValidation.setShowErrorBox(true);
            createValidation.setSuppressDropDownArrow(true);
        } else {
            createValidation.setSuppressDropDownArrow(false);
        }

        // 将sheet设置为隐藏
        workbook.setSheetHidden(sheetHidden, true);
        sheet.addValidationData(createValidation);
    }
}
