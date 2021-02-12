package com.e2buy.item.pojo;

import java.util.List;

/**
 * @Author: zjwawu@163.com
 * @Date: 2021/2/10 21:13
 * @Desc:
 **/
public class SpuBo extends Spu{

    private String cname;

    private String bname;

    private List<Sku> skus;

    private SpuDetail spuDetail;

    public SpuDetail getSpuDetail() {
        return spuDetail;
    }

    public void setSpuDetail(SpuDetail spuDetail) {
        this.spuDetail = spuDetail;
    }

    public List<Sku> getSkus() {
        return skus;
    }

    public void setSkus(List<Sku> skus) {
        this.skus = skus;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getBname() {
        return bname;
    }

    public void setBname(String bname) {
        this.bname = bname;
    }
}
