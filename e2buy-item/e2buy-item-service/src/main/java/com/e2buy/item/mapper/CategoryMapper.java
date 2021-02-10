package com.e2buy.item.mapper;

import com.e2buy.item.pojo.Category;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @Author: zjwawu@163.com
 * @Date: 2021/2/8 17:13
 * @Desc:
 **/
public interface CategoryMapper extends Mapper<Category>, SelectByIdListMapper<Category,Long> {


}
