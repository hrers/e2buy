package com.e2buy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class E2buyItemApplication {
    public static void main(String[] args) {
        SpringApplication.run(E2buyItemApplication.class, args);
    }
}
