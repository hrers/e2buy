package com.e2buy.item.service;

/**
 * @Author: zjwawu@163.com
 * @Date: 2021/2/10 14:23
 * @Desc:
 **/

import com.e2buy.item.pojo.SpecGroup;

import java.util.List;

/**
 * 给SpecGroup 和 SpecParam 服务
 */
public interface SpecificationService {
    List<SpecGroup> queryGroupsByCid(Long cid);
}
