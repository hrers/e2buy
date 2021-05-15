package com.e2buy.item.service.impl;

import com.e2buy.item.mapper.SpecGroupMapper;
import com.e2buy.item.mapper.SpecParamMapper;
import com.e2buy.item.pojo.SpecGroup;
import com.e2buy.item.pojo.SpecParam;
import com.e2buy.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
     * @param cid
     * @param generic
     * @param searching
     * @return
     */
    @Override
    public List<SpecParam> queryParams(Long gid, Long cid, Boolean generic, Boolean searching) {

        SpecParam record= new SpecParam();
        record.setCid(cid);
        record.setGroupId(gid);
        record.setGeneric(generic);
        record.setSearching(searching);
        return specParamMapper.select(record);
    }

    @Override
    public List<SpecGroup> queryGroupsWithParam(Long cid) {
        List<SpecGroup> groups = this.queryGroupsByCid(cid);
        groups.forEach(group->{
            List<SpecParam> params= this.queryParams(group.getId(), null, null, null);
            group.setParams(params);
        });
        return groups;
    }


    /**
     * 新增规格参数模板
     * @param specParam
     */
    @Override
    public void saveParam(SpecParam specParam) {
        this.specParamMapper.insert(specParam);
    }

    /**
     * 更新规格参数模板
     * @param specParam
     */
    @Override
    public void updateSpecParam(SpecParam specParam) {
            this.specParamMapper.updateByPrimaryKeySelective(specParam);
    }

    /**
     * 删除规格参数模板
     * @param specParam
     */
    @Override
    public void deleteSpecification(SpecParam specParam) {
        this.specParamMapper.deleteByPrimaryKey(specParam);
    }

    /**
     * 新增分组
     * @param specGroup
     */
    @Override
    public void saveSpecGroup(@RequestBody SpecGroup specGroup) {
        this.groupMapper.insert(specGroup);
    }

    /**
     * 删除分组
     * @param id
     */
    @Override
    public void deleteSpecGroup(Long id) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setId(id);
        this.groupMapper.delete(specGroup);
    }

    /**
     * 更新分组信息
     * @param specGroup
     */
    @Override
    public void updateSpecGroup(SpecGroup specGroup) {
        groupMapper.updateByPrimaryKeySelective(specGroup);
    }

}
