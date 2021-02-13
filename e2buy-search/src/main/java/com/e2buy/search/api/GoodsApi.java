package com.e2buy.search.api;

import com.e2buy.common.pojo.PageResult;
import com.e2buy.item.pojo.Sku;
import com.e2buy.item.pojo.SpuBo;
import com.e2buy.item.pojo.SpuDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author: zjwawu@163.com
 * @Date: 2021/2/13 14:04
 * @Desc: 定义feign调用方法，各个微服务都可以继承此接口，解决代码冗余
 *          返回值适当改变，将responseEntity不用包装返回值，避免解包麻烦
 **/
public interface GoodsApi {
    /**
     * 根据spuId查询spuDetail
     * @param spuId
     * @return
     */
    //feign 调用哪个方法就和对应controller里的方法一样即可,返回值可以改变
    @GetMapping("spu/detail/{spuId}")
    public SpuDetail querySpuDetailBySpuId(@PathVariable("spuId")Long spuId);


    /**
     * 根据条件分页查询spu
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */

    @GetMapping("spu/page")
    public PageResult<SpuBo> querySpuBoByPage(
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "saleable",required = false) Boolean saleable,
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows
    );


    /**
     *
     * @param spuId
     * @return
     */
    @GetMapping("sku/list")
    public List<Sku> querySkusBySpuId(@RequestParam("id") Long spuId);





}
