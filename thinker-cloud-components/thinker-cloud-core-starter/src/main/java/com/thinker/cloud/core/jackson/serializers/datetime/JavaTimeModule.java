package com.thinker.cloud.core.jackson.serializers.datetime;

import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Java Time序列化模块
 *
 * @author admin
 **/
public class JavaTimeModule extends SimpleModule {

    @Serial
    private static final long serialVersionUID = -9029872512752022050L;

    public JavaTimeModule() {
        // ======================= 时间序列化规则 ===============================
        // yyyy-MM-dd HH:mm:ss
        this.addSerializer(LocalDateTime.class, LocalDateTimeSerializer.INSTANCE);
        // yyyy-MM-dd
        this.addSerializer(LocalDate.class, LocalDateSerializer.INSTANCE);
        // HH:mm:ss
        this.addSerializer(LocalTime.class, LocalTimeSerializer.INSTANCE);

        // ======================= 时间反序列化规则 ==============================
        // yyyy-MM-dd HH:mm:ss
        this.addDeserializer(LocalDateTime.class, LocalDateTimeDeserializer.INSTANCE);
        // yyyy-MM-dd
        this.addDeserializer(LocalDate.class, LocalDateDeserializer.INSTANCE);
        // HH:mm:ss
        this.addDeserializer(LocalTime.class, LocalTimeDeserializer.INSTANCE);
    }

}
