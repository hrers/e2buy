package com.e2buy.goods.controller;

import com.e2buy.goods.service.GoodsHtmlService;
import com.e2buy.goods.service.GoodsService;
import com.netflix.discovery.converters.Auto;
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
    @Autowired
    private GoodsHtmlService goodsHtmlService;




   @GetMapping("item/{id}.html")
   public String toItemPage(@PathVariable("id") Long id, Model model){
        //加载所需的数据
       Map<String, Object> map= goodsService.loadData(id);
        //把数据放入数据模型
       model.addAllAttributes(map);
       goodsHtmlService.createHtml(id);
        //页面静态化
        return "item";
   }


   @GetMapping("item/stock/{id}")
   public Integer getSkuNum(@PathVariable("id") Long id){
       return goodsService.getSkuNum(id);
   }




}
