package com.e2buy.item.controller;

import com.e2buy.item.pojo.SpecGroup;
import com.e2buy.item.pojo.SpecParam;
import com.e2buy.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: zjwawu@163.com
 * @Date: 2021/2/10 14:26
 * @Desc:
 **/
@Controller
@RequestMapping("spec")
public class SpecificationController {


    @Autowired
    private SpecificationService specificationService;


    /**
     * 根据分类Id查询参数组
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsByCid(@PathVariable("cid")Long cid){

        List<SpecGroup> groups=specificationService.queryGroupsByCid(cid);

        if(CollectionUtils.isEmpty(groups)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groups);
    }


    /**
     * 根据条件查询规格参数
     * @param gid
     * @return
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParams(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "generic",required = false) Boolean generic,
            @RequestParam(value = "searching",required = false) Boolean searching
    ){
        List<SpecParam> params=specificationService.queryParams(gid,cid,generic,searching);
        if(CollectionUtils.isEmpty(params)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(params);
    }

    /**
     * 保存一个规格参数模板
     * @param specParam
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> saveSpecification(SpecParam specParam){
        this.specificationService.saveParam(specParam);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    /**
     * 根据分类查询参数组
     * @param cid
     * @return
     */
    @GetMapping("group/param/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupWithParam(@PathVariable("cid") Long cid){
        List<SpecGroup> groups=this.specificationService.queryGroupsWithParam(cid);
        if(CollectionUtils.isEmpty(groups)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groups);
    }

    /**
     * 修改一个规格参数模板
     * @param specParam
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateSpecification(SpecParam specParam){
        this.specificationService.updateSpecParam(specParam);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    /**
     * 删除规格参数模板
     * @param id
     * @return
     */
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteSpecification(@PathVariable("id") Long id){
        SpecParam specParam= new SpecParam();
        specParam.setCid(id);
        this.specificationService.deleteSpecification(specParam);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    //todo 后端接口没测试，前段页面还有调
}
