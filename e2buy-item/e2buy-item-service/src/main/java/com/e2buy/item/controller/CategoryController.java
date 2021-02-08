package com.e2buy.item.controller;

import com.e2buy.item.pojo.Category;
import com.e2buy.item.service.CategoryService;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: zjwawu@163.com
 * @Date: 2021/2/8 17:21
 * @Desc:
 **/
@Controller
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据父节点id查询子节点
     * @param pid
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategorysById(@RequestParam(value = "pid",defaultValue = "0")Long pid){
        if(pid==null|| pid<0){
            // 400：参数不合法
            //return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            //return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().build();

        }
        List<Category> categories=categoryService.queryCategoriesById(pid);

        if(CollectionUtils.isEmpty(categories)){
            // 404 资源服务器未找到
            //return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            return ResponseEntity.notFound().build();
        }
        // 200 查询成功
        return ResponseEntity.ok(categories);
        //500 不用try-catch响应，默认遇到错误就会响应500
    }
}
