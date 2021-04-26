package com.e2buy.item.service;

import com.e2buy.common.pojo.PageResult;
import com.e2buy.item.pojo.Sku;
import com.e2buy.item.pojo.Spu;
import com.e2buy.item.pojo.SpuBo;
import com.e2buy.item.pojo.SpuDetail;

import java.util.List;

/**
 * @Author: zjwawu@163.com
 * @Date: 2021/2/10 21:03
 * @Desc:
 **/
public interface GoodsService{
    PageResult<SpuBo> querySpuBoByPage(String key, Boolean saleable, Integer page, Integer rows);

    void saveGoods(SpuBo spuBo);

    SpuDetail querySpuDetailBySpuId(Long spuId);

    List<Sku> querySkusBySpuId(Long spuId);

    void updateGoods(SpuBo spuBo);

    Spu querySpuById(Long id);

    Sku querySkuBySkuId(Long skuId);

    void sendMessage(Long id,String type);

    void goodsSoldOut(long id);
}
