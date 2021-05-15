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

    List<String> queryNamesByIds(List<Long> asList);

    List<Category> queryByBrandId(Long bid);

    void saveCategory(Category category);

    void updateCategory(Category category);

    void deleteCategory(Long id);
}
