package com.e2buy.item.service.impl;

import com.e2buy.common.pojo.PageResult;
import com.e2buy.item.mapper.BrandMapper;
import com.e2buy.item.pojo.Brand;
import com.e2buy.item.service.BrandService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @Author: zjwawu@163.com
 * @Date: 2021/2/8 23:47
 * @Desc:
 **/

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    /**
     * 根据查询条件分页并排序，查询品牌信息
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc  true:desc  false:asc
     * @return
     */
    @Override
    public PageResult<Brand> queryBrandsByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {

        // 初始化example对象
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();

        // 根据name模糊查询，或者根据首字母查询
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("name", "%" + key + "%").orEqualTo("letter", key);
        }
        // 添加分页条件
        PageHelper.startPage(page, rows);

        // 添加排序条件
        if (StringUtils.isNotBlank(sortBy)) {
            example.setOrderByClause(sortBy + " " + (desc ? "desc" : "asc"));
        }

        List<Brand> brands = this.brandMapper.selectByExample(example);
        // 包装成pageInfo
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);
        // 包装成分页结果集返回
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 新增品牌
     * @param brand
     * @param cids
     */
    @Override
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        //先新增brand
        boolean flag = this.brandMapper.insertSelective(brand)==1;

        //再新增中间表
        cids.forEach(cid->{
           this.brandMapper.insertCategoryAndBrand(cid,brand.getId());
        });
    }

    /**
     * 通过分类id查询品牌列表
     * @param cid
     * @return
     */
    @Override
    public List<Brand> queryBrandsByCid(Long cid) {
        return brandMapper.selectBrandsByCid(cid);
    }

    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    @Override
    public Brand queryBrandById(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    /**
     * 品牌更新
     * @param brand
     * @param categories
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateBrand(Brand brand,List<Long> categories) {
        //删除原来的数据
        deleteByBrandIdInCategoryBrand(brand.getId());

        // 修改品牌信息
        this.brandMapper.updateByPrimaryKeySelective(brand);

        //维护品牌和分类中间表
        for (Long cid : categories) {
            //todo 循环里面写sql操作非常不可取，后期重写
            this.brandMapper.insertCategoryBrand(cid, brand.getId());
        }
    }

    /**
     * 删除中间表中的数据
     * @param bid
     */
    @Override
    public void deleteByBrandIdInCategoryBrand(Long bid) {
        this.brandMapper.deleteByBrandIdInCategoryBrand(bid);
    }

    /**
     *
     * @param bid
     */
    @Override
    public void deleteBrand(long bid) {
        //删除品牌信息
        this.brandMapper.deleteByPrimaryKey(bid);
        //维护中间表
        this.brandMapper.deleteByBrandIdInCategoryBrand(bid);
    }



}
