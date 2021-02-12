package com.e2buy.item.mapper;

import com.e2buy.item.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Author: zjwawu@163.com
 * @Date: 2021/2/8 23:46
 * @Desc:
 **/
public interface BrandMapper extends Mapper<Brand> {

    @Insert("INSERT INTO tb_category_brand (category_id,brand_id) VALUES (#{cid},#{bid})")
    void insertCategoryAndBrand(@Param("cid") Long cid,@Param("bid") Long id);

    @Select("SELECT * FROM  tb_brand a INNER JOIN tb_category_brand b ON a.id=b.brand_id WHERE b.category_id=#{cid}")
    List<Brand> selectBrandsByCid(Long cid);


}
