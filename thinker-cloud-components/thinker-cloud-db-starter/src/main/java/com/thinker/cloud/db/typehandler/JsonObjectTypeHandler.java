package com.thinker.cloud.db.typehandler;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.thinker.cloud.core.utils.JsonCodec;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * Json 转 Object 类型处理
 *
 * @author admin
 */
@NoArgsConstructor
@MappedTypes(Object.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class JsonObjectTypeHandler extends AbstractJsonTypeHandler<Object> {

    private final Class<Object> clazz = Object.class;

    @Override
    protected Object parse(String json) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(json);
            String className = jsonObject.getString("@_class");
            Class<?> aClass = Class.forName(className);
            return JsonCodec.decode(json, aClass);
        } catch (Exception e) {
            return JsonCodec.decode(json, clazz);
        }
    }

    @Override
    protected String toJson(Object obj) {
        return obj != null ? JsonCodec.encode(obj) : null;
    }
}
