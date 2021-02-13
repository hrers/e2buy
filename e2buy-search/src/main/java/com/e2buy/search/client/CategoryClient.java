package com.e2buy.search.client;

import com.e2buy.search.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: zjwawu@163.com
 * @Date: 2021/2/13 14:22
 * @Desc:
 **/
@FeignClient("item-service")
public interface CategoryClient extends CategoryApi {
}
