package com.e2buy.item.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author: zjwawu@163.com
 * @Date: 2021/2/8 17:21
 * @Desc:
 **/
@RequestMapping("category")
public interface CategoryApi {

    @GetMapping
    public List<String> queryNamesByIds(@RequestParam("ids")List<Long> ids);

}
