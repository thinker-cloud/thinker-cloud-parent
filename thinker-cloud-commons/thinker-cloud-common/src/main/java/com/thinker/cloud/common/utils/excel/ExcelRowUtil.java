package com.thinker.cloud.common.utils.excel;

import cn.hutool.core.util.NumberUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.math.BigDecimal;

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
            String numericCellValue = String.valueOf(cell.getNumericCellValue());
            // 避免数字太大导致变成科学计数
            if (isScientificNotation(numericCellValue)) {
                return NumberUtil.toStr(new BigDecimal(numericCellValue));
            }
            return numericCellValue;
        }
    }

    private static boolean isScientificNotation(String number) {
        return number.toLowerCase().contains("e");
    }
}
