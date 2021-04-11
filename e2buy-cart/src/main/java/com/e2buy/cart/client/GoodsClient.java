package com.e2buy.cart.client;

import com.e2buy.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: zhangjianwu
 * @Date: 2021/4/11 22:34
 * @Desc:
 **/

@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {
}
