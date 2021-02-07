package com.e2buy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class E2buyRegistryApplication {
    public static void main(String[] args) {
        SpringApplication.run(E2buyRegistryApplication.class, args);
    }
}
