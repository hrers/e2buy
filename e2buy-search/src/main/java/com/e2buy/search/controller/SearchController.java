package com.e2buy.search.controller;

import com.e2buy.common.pojo.PageResult;
import com.e2buy.search.pojo.Goods;
import com.e2buy.search.pojo.SearchRequest;
import com.e2buy.search.service.SearchService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author: zjwawu@163.com
 * @Date: 2021/2/14 3:00
 * @Desc:
 **/
@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;


    /**
     * 商品页面查询
     * 使用post请求方便传递更多的请求参数
     * @param request
     * @return
     */
    @PostMapping("page")
    public ResponseEntity<PageResult<Goods>> search(@RequestBody SearchRequest request){
        PageResult<Goods> result=searchService.search(request);
        if(result==null|| CollectionUtils.isEmpty(result.getItems())){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }


}
