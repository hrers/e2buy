package com.e2buy.dto;

/**
 * @Author: zhangjianwu
 * @Date: 2021/5/20 17:59
 * @Desc:
 **/

public class SaleResult {
    private Long todayMoney;
    private Long totalMoney;
    private Long[] sales;

    public Long getTodayMoney() {
        return todayMoney;
    }

    public void setTodayMoney(Long todayMoney) {
        this.todayMoney = todayMoney;
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
