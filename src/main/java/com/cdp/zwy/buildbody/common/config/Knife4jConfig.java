package com.cdp.zwy.buildbody.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zwy
 * @version 1.0
 * @description: knife4jConfig
 * @date 2025/9/24 21:37
 */
@Configuration
public class Knife4jConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API文档")
                        .version("1.0")
                        .description("基于SpringBoot3 + Vue3开发的智能健身系统项目")
                        .contact(new Contact().name("weiyou").url("http://47.108.140.119:80/").email("3429317309@qq.com"))
                );
    }
}