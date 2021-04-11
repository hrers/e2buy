package com.e2buy.goods.client;

import com.e2buy.item.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: zjwawu@163.com
 * @Date: 2021/2/13 14:19
 * @Desc:
 **/
@FeignClient("item-service")
public interface BrandClient extends BrandApi {

}
