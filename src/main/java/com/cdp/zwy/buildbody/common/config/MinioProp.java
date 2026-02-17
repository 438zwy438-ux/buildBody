package com.cdp.zwy.buildbody.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zwy
 * @version 1.0
 * @description: MinioProp读取配置
 * * @date 2026/2/17 11:00
 */
@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioProp {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
}