package com.e2buy.dto;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Author: zhangjianwu
 * @Date: 2021/5/20 17:59
 * @Desc:
 **/

public class SaleResult {
    private Long todayMoney;
    private Long toweekMoney;
    private Long tomonthMoney;
    private Long toyearMoney;
    private Long totalMoney;
    private Long[] sales;
    private List<PlaceDto> places;

    public List<PlaceDto> getPlaces() {
        return places;
    }

    public void setPlaces(List<PlaceDto> places) {
        this.places = places;
    }

    public Long getTodayMoney() {
        return todayMoney;
    }

    public void setTodayMoney(Long todayMoney) {
        this.todayMoney = todayMoney;
    }

    public Long getToweekMoney() {
        return toweekMoney;
    }

    public void setToweekMoney(Long toweekMoney) {
        this.toweekMoney = toweekMoney;
    }

    public Long getTomonthMoney() {
        return tomonthMoney;
    }

    public void setTomonthMoney(Long tomonthMoney) {
        this.tomonthMoney = tomonthMoney;
    }

    public Long getToyearMoney() {
        return toyearMoney;
    }

    public void setToyearMoney(Long toyearMoney) {
        this.toyearMoney = toyearMoney;
    }

    public Long getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(Long totalMoney) {
        this.totalMoney = totalMoney;
    }

    public Long[] getSales() {
        return sales;
    }

    public void setSales(Long[] sales) {
        this.sales = sales;
    }
}
