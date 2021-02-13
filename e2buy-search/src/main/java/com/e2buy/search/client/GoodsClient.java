package com.e2buy.search.client;

import com.e2buy.search.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: zjwawu@163.com
 * @Date: 2021/2/13 13:58
 * @Desc:
 **/
@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {


}
