package com.e2buy.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class E2buyAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(E2buyAuthApplication.class, args);
    }
}
