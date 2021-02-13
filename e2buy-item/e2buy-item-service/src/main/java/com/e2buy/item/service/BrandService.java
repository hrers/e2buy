package com.e2buy.item.service;

import com.e2buy.common.pojo.PageResult;
import com.e2buy.item.pojo.Brand;

import java.util.List;

/**
 * @Author: zjwawu@163.com
 * @Date: 2021/2/8 23:47
 * @Desc:
 **/
public interface BrandService {
    /**
     * 根据查询条件分页并排序，查询品牌信息
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    PageResult<Brand> queryBrandsByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc);

    /**
     * 新增品牌
     * @param brand
     * @param cids
     */
    void saveBrand(Brand brand, List<Long> cids);

    /**
     * 通过分类id查询品牌列表
     * @param cid
     * @return
     */
    List<Brand> queryBrandsByCid(Long cid);

    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    Brand queryBrandById(Long id);
}
