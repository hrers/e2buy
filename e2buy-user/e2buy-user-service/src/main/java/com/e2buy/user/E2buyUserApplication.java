package com.e2buy.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.e2buy.user.mapper")
public class E2buyUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(E2buyUserApplication.class, args);
    }
}
