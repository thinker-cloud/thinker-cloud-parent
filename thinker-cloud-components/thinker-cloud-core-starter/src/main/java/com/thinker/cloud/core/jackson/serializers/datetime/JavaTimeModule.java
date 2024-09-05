package com.thinker.cloud.core.jackson.serializers.datetime;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.PackageVersion;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;

import java.io.Serial;
import java.time.Instant;
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
        super(PackageVersion.VERSION);

        // ======================= 时间序列化规则 ===============================
        // yyyy-MM-dd HH:mm:ss
        this.addSerializer(LocalDateTime.class, LocalDateTimeSerializer.INSTANCE);
        // yyyy-MM-dd
        this.addSerializer(LocalDate.class, LocalDateSerializer.INSTANCE);
        // HH:mm:ss
        this.addSerializer(LocalTime.class, LocalTimeSerializer.INSTANCE);
        // Instant 类型序列化
        this.addSerializer(Instant.class, InstantSerializer.INSTANCE);

        // ======================= 时间反序列化规则 ==============================
        // yyyy-MM-dd HH:mm:ss
        this.addDeserializer(LocalDateTime.class, LocalDateTimeDeserializer.INSTANCE);
        // yyyy-MM-dd
        this.addDeserializer(LocalDate.class, LocalDateDeserializer.INSTANCE);
        // HH:mm:ss
        this.addDeserializer(LocalTime.class, LocalTimeDeserializer.INSTANCE);
        // Instant 反序列化
        this.addDeserializer(Instant.class, InstantDeserializer.INSTANT);
    }

}
