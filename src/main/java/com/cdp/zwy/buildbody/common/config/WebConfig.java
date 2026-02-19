package com.cdp.zwy.buildbody.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * @author zwy
 * @version 1.0
 * @description: Web配置类，处理JSON序列化时区问题
 * @date 2026/2/19
 */
@Configuration
public class WebConfig {

    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        
        // 设置日期时间格式
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        // 设置序列化器和反序列化器，使用东八区时区
        javaTimeModule.addSerializer(LocalDateTime.class, 
                new LocalDateTimeSerializer(dateTimeFormatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, 
                new LocalDateTimeDeserializer(dateTimeFormatter));
        
        return builder
                .modules(javaTimeModule)
                .timeZone(TimeZone.getTimeZone(ZoneId.of("Asia/Shanghai"))) // 设置时区为东八区
                .build();
    }
}