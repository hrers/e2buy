package com.e2buy.search.repository;

import com.e2buy.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author: zjwawu@163.com
 * @Date: 2021/2/13 17:22
 * @Desc:
 **/
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}
