package com.e2buy.search.api;

import com.e2buy.item.pojo.Brand;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: zjwawu@163.com
 * @Date: 2021/2/8 23:47
 * @Desc:
 **/
@RequestMapping("brand")
public interface BrandApi{

    @GetMapping("/{id}")
    public Brand queryBrandById(@PathVariable("id")Long id);

}
