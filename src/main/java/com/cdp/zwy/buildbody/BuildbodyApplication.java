package com.cdp.zwy.buildbody;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.cdp.zwy.buildbody.module.**.dao")
public class BuildbodyApplication {
    public static void main(String[] args) {
        SpringApplication.run(BuildbodyApplication.class, args);
    }

}