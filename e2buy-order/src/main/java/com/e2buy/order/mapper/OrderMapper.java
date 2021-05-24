package com.e2buy.order.mapper;

import com.e2buy.dto.PlaceDto;
import com.e2buy.order.pojo.Order;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public interface OrderMapper extends Mapper<Order> {

    List<Order> queryOrderList(
            @Param("userId") Long userId,
            @Param("status") Integer status);

    List<Order> queryOrderListBack(
            @Param("status") Integer status);

    List<Order> selectWithStatus(@Param("status") Integer status);

    Integer querySaledSkuNumBySkuId(@Param("skuId")Long skuId);

    @Select("SELECT receiver_state city,COUNT(*) num FROM tb_order GROUP BY receiver_state ORDER BY num DESC  LIMIT 0,5")
    List<PlaceDto> selectWithCity();
}
