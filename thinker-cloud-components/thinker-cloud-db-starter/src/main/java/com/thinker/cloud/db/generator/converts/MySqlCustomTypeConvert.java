package com.thinker.cloud.db.generator.converts;

import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import org.apache.ibatis.type.JdbcType;
import org.jetbrains.annotations.NotNull;

/**
 * MYSQL 自定义数据库字段类型转换
 *
 * @author admin
 **/
public class MySqlCustomTypeConvert extends MySqlTypeConvert {

    public static final MySqlCustomTypeConvert INSTANCE = new MySqlCustomTypeConvert();

    @Override
    public IColumnType processTypeConvert(@NotNull GlobalConfig globalConfig, @NotNull TableField tableField) {
        // 优先默认
        IColumnType processed = super.processTypeConvert(globalConfig, tableField);

        // 自定义处理
        if (DbColumnType.BYTE.equals(processed)) {
            // TINYINT > 1
            String tinyint = JdbcType.TINYINT.name().toLowerCase();
            if (tableField.getType().startsWith(tinyint)) {
                return DbColumnType.INTEGER;
            }
        }
        return processed;
    }
}
