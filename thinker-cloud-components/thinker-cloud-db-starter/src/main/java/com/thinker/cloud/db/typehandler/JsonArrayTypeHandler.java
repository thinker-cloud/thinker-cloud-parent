package com.thinker.cloud.db.typehandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * Json 转 JSONArray 类型处理
 *
 * @author admin
 */
@NoArgsConstructor
@MappedTypes(JSONArray.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class JsonArrayTypeHandler extends AbstractJsonTypeHandler<JSONArray> {

    @Override
    protected JSONArray parse(String json) {
        return JSON.parseArray(json);
    }

    @Override
    protected String toJson(JSONArray json) {
        return json != null ? json.toJSONString() : null;
    }
}
