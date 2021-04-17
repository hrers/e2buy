package com.e2buy.config;

import com.e2buy.common.utils.IdWorker;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(com.e2buy.config.IdWorkerProperties.class)
public class IdWorkerConfig {

    @Bean
    public IdWorker idWorker(com.e2buy.config.IdWorkerProperties prop) {
        return new IdWorker(prop.getWorkerId(), prop.getDatacenterId());
    }
}
