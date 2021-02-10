package com.e2buy.item.service.impl;

import com.e2buy.common.pojo.PageResult;
import com.e2buy.item.mapper.BrandMapper;
import com.e2buy.item.mapper.SpuDetailMapper;
import com.e2buy.item.mapper.SpuMapper;
import com.e2buy.item.pojo.Brand;
import com.e2buy.item.pojo.Category;
import com.e2buy.item.pojo.Spu;
import com.e2buy.item.pojo.SpuBo;
import com.e2buy.item.service.CategoryService;
import com.e2buy.item.service.GoodsService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.netflix.discovery.converters.Auto;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: zjwawu@163.com
 * @Date: 2021/2/10 21:03
 * @Desc:
 **/
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandMapper brandMapper;




    @Override
    public PageResult<SpuBo> querySpuByPage(String key, Boolean saleable, Integer page, Integer rows) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //搜索条件
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }
        if(saleable != null){
           criteria.andEqualTo("saleable",saleable);
        }
        //分页条件
        PageHelper.startPage(page,rows);
        //执行查询
        List<Spu> spus = spuMapper.selectByExample(example);
        PageInfo<Spu> pageInfo = new PageInfo<>(spus);

        //spu集合转化为spubo集合 1
//        List<SpuBo> spuBos = new ArrayList<>();
//        spus.forEach(spu -> {
//            SpuBo spuBo = new SpuBo();
//            //copy共同属性到新的对象
//            BeanUtils.copyProperties(spu,spuBo);
//            //查询分类名称
//            List<String> names = categoryService.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
//
//            spuBo.setCname(StringUtils.join(names,"/"));
//
//            //查询品牌的名称
//            spuBo.setBname(this.brandMapper.selectByPrimaryKey(spu.getBrandId()).getName());
//            spuBos.add(spuBo);
//        });

        //spu集合转化为spuBo集合 2
        List<SpuBo> spuBos = spus.stream().map(spu -> {
            SpuBo spuBo = new SpuBo();
            BeanUtils.copyProperties(spu, spuBo);
            //查询品牌名称
            Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
            spuBo.setBname(brand.getName());
            //查询分类名称
            List<String> names = categoryService.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            spuBo.setCname(StringUtils.join(names, "/"));
            return spuBo;
        }).collect(Collectors.toList());


        return new PageResult<>(pageInfo.getTotal(),spuBos);
    }
}
