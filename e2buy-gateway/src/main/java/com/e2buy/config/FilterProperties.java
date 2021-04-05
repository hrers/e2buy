package com.e2buy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @Author: zhangjianwu
 * @Date: 2021/4/5 22:38
 * @Desc:
 **/
@ConfigurationProperties(prefix = "e2buy.filter")
public class FilterProperties {

    private List<String> allowPaths;

    public List<String> getAllowPaths() {
        return allowPaths;
    }

    public void setAllowPaths(List<String> allowPaths) {
        this.allowPaths = allowPaths;
    }
}