package com.cdp.zwy.buildbody.common.config;

import io.minio.MinioClient;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zwy
 * @version 1.0
 * @description: MinioConfig
 * @date 2026/2/17 11:01
 */
@Configuration
public class MinioConfig {
    @Resource
    private MinioProp minioProp;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioProp.getEndpoint())
                .credentials(minioProp.getAccessKey(), minioProp.getSecretKey())
                .build();
    }
}