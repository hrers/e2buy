package com.e2buy.cart.config;

import com.e2buy.cart.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: zhangjianwu
 * @Date: 2021/4/11 16:55
 * @Desc:
 **/
@Configuration
public class E2buyWebMvcConfiguration  implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    //添加拦截器,拦截所有路径
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**");
    }
}

