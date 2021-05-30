package com.e2buy.order.service;

import com.e2buy.order.mapper.EvaluateMapper;
import com.e2buy.order.mapper.OrderStatusMapper;
import com.e2buy.order.pojo.Evaluate;
import com.e2buy.order.pojo.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;

/**
 * @Author: zhangjianwu
 * @Date: 2021/5/22 13:06
 * @Desc:
 **/
@Service
public class EvaluateService {

    @Autowired
    private EvaluateMapper evaluateMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    public void createEvaluate(Evaluate evaluate) {
        evaluate.setCreateTime(new Date());
        //若是评论过了就将订单标志设置 status:6已评价
        OrderStatus example = new OrderStatus();
        example.setOrderId(evaluate.getOrderId());
        //已评论
        example.setStatus(6);
        orderStatusMapper.updateByPrimaryKeySelective(example);
        evaluateMapper.insertSelective(evaluate);
    }

    public Evaluate queryEvaluateByOrderId(Long orderId) {
        Evaluate example = new Evaluate();
        example.setOrderId(orderId);
        return evaluateMapper.selectOne(example);
    }
}
