package com.e2buy.order.service.api;

import com.e2buy.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "e2buy-gateway", path = "/api/item")
public interface GoodsService extends GoodsApi {
}
