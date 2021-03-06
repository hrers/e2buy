package com.e2buy.goods.controller;

import com.e2buy.goods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @Author: zjwawu@163.com
 * @Date: 2021/3/6 13:30
 * @Desc:
 **/
@Controller
public class GoodsController {

    @Autowired
    private GoodsService goodsService;
   @GetMapping("item/{id}.html")
   public String toItemPage(@PathVariable("id") Long id, Model model){
       Map<String, Object> map= goodsService.loadData(id);
       model.addAllAttributes(map);
        return "item";
   }



}
