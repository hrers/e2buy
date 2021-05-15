package com.e2buy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDiscoveryClient
//扫描通用mapper扫描注解
@MapperScan("com.e2buy.item.mapper")
public class E2buyItemApplication {
    public static void main(String[] args) {
        SpringApplication.run(E2buyItemApplication.class, args);
    }
}
