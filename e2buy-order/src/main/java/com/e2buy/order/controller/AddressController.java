package com.e2buy.order.controller;

import com.e2buy.order.pojo.Address;
import com.e2buy.order.service.AddressService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: zhangjianwu
 * @Date: 2021/5/23 11:04
 * @Desc:
 **/
@RestController
@RequestMapping("order/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * 获取全部地址信息
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Address>> getAddresses(){
        List<Address> list=addressService.getAllAddress();
        return ResponseEntity.ok(list);
    }


    @PostMapping
    public ResponseEntity<Void> createAddress(@RequestBody Address address){
       addressService.createNewAddress(address);
       return ResponseEntity.ok().build();
    }




}
