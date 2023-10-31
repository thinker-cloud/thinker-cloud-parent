package com.thinker.cloud.core.utils.excel;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * Excel 读行工具
 *
 * @author admin
 */
@Slf4j
@UtilityClass
public class ExcelRowUtil {

    /**
     * 获取列的值
     *
     * @param readRow   readRow
     * @param cellIndex cellIndex
     * @return String
     */
    public static String getCellValue(Row readRow, int cellIndex) {
        if (readRow == null) {
            return null;
        }
        Cell cell = readRow.getCell(cellIndex);
        if (cell == null) {
            return null;
        }
        try {
            return cell.getStringCellValue().trim();
        } catch (IllegalStateException e) {
            return String.valueOf(cell.getNumericCellValue());
        }
    }
}
