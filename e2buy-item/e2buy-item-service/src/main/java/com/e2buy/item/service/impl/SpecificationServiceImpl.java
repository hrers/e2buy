package com.e2buy.item.service.impl;

import com.e2buy.item.mapper.SpecGroupMapper;
import com.e2buy.item.mapper.SpecParamMapper;
import com.e2buy.item.pojo.SpecGroup;
import com.e2buy.item.pojo.SpecParam;
import com.e2buy.item.service.SpecificationService;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: zjwawu@163.com
 * @Date: 2021/2/10 14:23
 * @Desc:
 **/
@Service
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private SpecGroupMapper groupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;




    /**
     * 分局分类id查询参数组
     * @param cid
     * @return
     */
    @Override
    public List<SpecGroup> queryGroupsByCid(Long cid) {
        SpecGroup record = new SpecGroup();
        record.setCid(cid);
        return groupMapper.select(record);
    }

    /**
     * 根据条件查询规格参数
     * @param gid
     * @return
     */
    @Override
    public List<SpecParam> queryParams(Long gid) {
        SpecParam record= new SpecParam();
        record.setGroupId(gid);
        return specParamMapper.select(record);
    }




}
