package com.thinker.cloud.core.excel;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.handler.inter.IWriter;
import com.thinker.cloud.core.utils.thread.BatchTaskUtil;
import lombok.Cleanup;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileOutputStream;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.LongFunction;

/**
 * Excel导出任务对象
 *
 * @author admin
 **/
@Slf4j
@Data
@Accessors(chain = true)
public class ExcelExportTask<T> implements Callable<Boolean> {

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 单个Excel总数据量
     */
    private Long total;

    /**
     * 当前页码
     */
    private Long page;

    /**
     * 单页查询限制条数
     */
    private Long limit;

    /**
     * 数据对象Class
     */
    private Class<T> clazz;

    /**
     * 获取数据过程
     */
    private LongFunction<List<T>> supplier;


    public ExcelExportTask(String filePath, Class<T> clazz, LongFunction<List<T>> supplier) {
        this.filePath = filePath;
        this.clazz = clazz;
        this.supplier = supplier;
    }


    @Override
    public Boolean call() {
        try {
            ExportParams exportParams = new ExportParams();
            exportParams.setType(ExcelType.XSSF);
            @Cleanup IWriter<Workbook> writer = ExcelExportUtil.exportBigExcel(exportParams, clazz);

            // 分批次查询并写入Excel
            BatchTaskUtil.batchQuery(total, limit, page -> {
                long startPage = this.page + page;
                return supplier.apply(startPage);
            }, writer::write);

            // 将数据输出到指定文件中
            @Cleanup FileOutputStream outputStream = new FileOutputStream(filePath);
            writer.get().write(outputStream);
        } catch (Exception e) {
            log.error("导出Excel异常，ex={}", e.getMessage(), e);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 设置文件存储数据总量
     *
     * @param total   文件存储数据总量
     * @param limit   分页查询 limit
     * @param fileNum 当前文件序号
     */
    public ExcelExportTask<T> generatePage(long total, long limit, int fileNum) {
        this.total = total;
        this.limit = limit;

        // 计算文件分页起始页码
        this.page = ((fileNum - 1) * total) / limit;
        return this;
    }
}
