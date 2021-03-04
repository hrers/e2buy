package com.e2buy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class E2buyGoodsWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(E2buyGoodsWebApplication.class, args);
    }
}
