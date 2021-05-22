package com.e2buy.order.pojo;

import javax.persistence.Table;
import java.util.Date;

/**
 * @Author: zhangjianwu
 * @Date: 2021/5/22 12:24
 * @Desc: 评论微服务暂时先不做，在订单微服务里面完成吧
 **/
@Table(name = "tb_evaluate")
public class Evaluate {

    private Long id;
    private Long orderId;
    private String message;
    private Date createTime;// 创建时间
    private Date updateTime;// 更新时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
