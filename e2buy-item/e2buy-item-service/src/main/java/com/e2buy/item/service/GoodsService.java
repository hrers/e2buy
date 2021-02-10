package com.e2buy.item.service;

import com.e2buy.common.pojo.PageResult;
import com.e2buy.item.pojo.SpuBo;

/**
 * @Author: zjwawu@163.com
 * @Date: 2021/2/10 21:03
 * @Desc:
 **/
public interface GoodsService{
    PageResult<SpuBo> querySpuByPage(String key, Boolean saleable, Integer page, Integer rows);
}
