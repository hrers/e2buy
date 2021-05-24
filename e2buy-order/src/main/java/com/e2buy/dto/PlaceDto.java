package com.e2buy.dto;

import java.net.Inet4Address;

/**
 * @Author: zhangjianwu
 * @Date: 2021/5/23 16:49
 * @Desc:
 **/

public class PlaceDto {
    private String city;
    private Integer num;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
