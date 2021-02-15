package com.e2buy.search.pojo;

import com.e2buy.common.pojo.PageResult;
import com.e2buy.item.pojo.Brand;

import java.util.List;
import java.util.Map;

/**
 * @Author: zjwawu@163.com
 * @Date: 2021/2/14 22:15
 * @Desc:
 **/
public class SearchResult extends PageResult<Goods> {

    private List<Map<String,Object>> categories;

    private List<Brand> brands;


    public List<Map<String, Object>> getCategories() {
        return categories;
    }

    public void setCategories(List<Map<String, Object>> categories) {
        this.categories = categories;
    }

    public List<Brand> getBrands() {
        return brands;
    }

    public void setBrands(List<Brand> brands) {
        this.brands = brands;
    }

    public SearchResult(List<Map<String, Object>> categories, List<Brand> brands) {
        this.categories = categories;
        this.brands = brands;
    }

    public SearchResult(Long total, List<Goods> items, List<Map<String, Object>> categories, List<Brand> brands) {
        super(total, items);
        this.categories = categories;
        this.brands = brands;
    }

    public SearchResult(Long total, Integer totalPage, List<Goods> items, List<Map<String, Object>> categories, List<Brand> brands) {
        super(total, totalPage, items);
        this.categories = categories;
        this.brands = brands;
    }

    public SearchResult() {
    }
}
