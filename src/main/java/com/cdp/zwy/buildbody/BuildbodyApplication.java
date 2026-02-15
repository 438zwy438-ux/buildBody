package com.cdp.zwy.buildbody;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication

@EnableScheduling
public class BuildbodyApplication {
    public static void main(String[] args) {
        SpringApplication.run(BuildbodyApplication.class, args);
    }

}