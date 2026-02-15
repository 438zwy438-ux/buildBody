package com.cdp.zwy.buildbody.common.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zwy
 * @version 1.0
 * @description: MybatisConfiguration
 * @date 2025/8/13 11:58
 */


@Configuration
public class MybatisConfiguration {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new MybatisPlusSqlLogInterceptor());
        return interceptor;
    }
}
