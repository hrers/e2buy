package com.e2buy.item.service;

/**
 * @Author: zjwawu@163.com
 * @Date: 2021/2/10 14:23
 * @Desc:
 **/

import com.e2buy.item.pojo.SpecGroup;
import com.e2buy.item.pojo.SpecParam;

import java.util.List;

/**
 * 给SpecGroup 和 SpecParam 服务
 */
public interface SpecificationService {
    List<SpecGroup> queryGroupsByCid(Long cid);

    /**
     * 根据条件查询规格参数
     * @param gid
     * @param cid
     * @param generic
     * @param searching
     * @return
     */
    List<SpecParam> queryParams(Long gid, Long cid, Boolean generic, Boolean searching);
}
