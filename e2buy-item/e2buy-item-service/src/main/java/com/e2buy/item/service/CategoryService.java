package com.e2buy.item.service;

import com.e2buy.item.pojo.Category;

import java.util.List;

/**
 * @Author: zjwawu@163.com
 * @Date: 2021/2/8 17:16
 * @Desc:
 **/
public interface CategoryService {
    List<Category> queryCategoriesById(Long pid);
}
