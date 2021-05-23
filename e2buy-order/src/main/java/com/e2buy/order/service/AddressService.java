package com.e2buy.order.service;

import com.e2buy.order.mapper.AddressMapper;
import com.e2buy.order.pojo.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: zhangjianwu
 * @Date: 2021/5/23 11:06
 * @Desc:
 **/
@Service
public class AddressService {

    @Autowired
    private AddressMapper addressMapper;

    public List<Address> getAllAddress() {
        return addressMapper.selectAll();
    }

    public void createNewAddress(Address address) {
        if(address.getIsDefault()==1){
            //将默认收货地址标志去除
            addressMapper.clearDefaultAddress();
        }
        addressMapper.insert(address);
    }
}
