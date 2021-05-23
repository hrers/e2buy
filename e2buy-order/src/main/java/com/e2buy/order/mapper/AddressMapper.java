package com.e2buy.order.mapper;

import com.e2buy.order.pojo.Address;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

/**
 * @Author: zhangjianwu
 * @Date: 2021/5/23 11:07
 * @Desc:
 **/
public interface AddressMapper extends Mapper<Address> {
    @Update("UPDATE  tb_address  SET tb_address.is_default=0")
    void clearDefaultAddress();
}
