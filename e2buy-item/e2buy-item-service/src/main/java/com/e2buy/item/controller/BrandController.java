package com.e2buy.item.controller;

import com.e2buy.common.pojo.PageResult;
import com.e2buy.item.pojo.Brand;
import com.e2buy.item.service.BrandService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: zjwawu@163.com
 * @Date: 2021/2/8 23:47
 * @Desc:
 **/
@Controller
@RequestMapping("brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    /**
     * 根据查询条件分页并排序，查询品牌信息
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc true:desc  false:asc
     * @return
     */
    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandsByPage(
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "page",defaultValue ="1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy",required = false)String sortBy,
            @RequestParam(value = "desc",required = false)Boolean desc
            ){
        PageResult<Brand> result=brandService.queryBrandsByPage(key,page,rows,sortBy,desc);
        if(CollectionUtils.isEmpty(result.getItems())){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }


    /**
     * 新增品牌
     * @param brand
     * @param cids
     * @return
     */
   @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand,@RequestParam("cids") List<Long> cids){
        this.brandService.saveBrand(brand,cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * 通过分类id查询品牌列表
     * @param cid
     * @return
     */
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandsByCid(@PathVariable("cid")Long cid){
       List<Brand> brands=brandService.queryBrandsByCid(cid);
       if(CollectionUtils.isEmpty(brands)){
           return ResponseEntity.notFound().build();
       }
       return ResponseEntity.ok(brands);

    }

    /**
     * 通过bid查询品牌
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("id")Long id){
        Brand brand=brandService.queryBrandById(id);
        if(brand == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brand);
    }

    /**
     * 品牌修改
     * @param brand
     * @param categories
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateBrand(Brand brand,@RequestParam("cids") List<Long> categories){
        this.brandService.updateBrand(brand,categories);
        return  ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }


    /**
     * 删除tb_brand中的数据,单个删除、多个删除二合一
     * @param bid
     * @return
     */
    @DeleteMapping("bid/{bid}")
    public ResponseEntity<Void> deleteBrand(@PathVariable("bid") String bid){
        String separator="-";
        if(bid.contains(separator)){
            String[] ids=bid.split(separator);
            for (String id:ids){
                this.brandService.deleteBrand(Long.parseLong(id));
            }
        }
        else {
            this.brandService.deleteBrand(Long.parseLong(bid));
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 删除tb_category_brand中的数据
     * @param bid
     * @return
     */
    @DeleteMapping("cid_bid/{bid}")
    public ResponseEntity<Void> deleteByBrandIdInCategoryBrand(@PathVariable("bid") Long bid){
        //System.out.println("删除中间表");
        this.brandService.deleteByBrandIdInCategoryBrand(bid);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
