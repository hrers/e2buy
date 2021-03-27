package com.e2buy.goods.listener;

import com.e2buy.goods.service.GoodsHtmlService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: zhangjianwu
 * @Date: 2021/3/28 3:15
 * @Desc:
 **/
@Component
public class GoodsListener {

    @Autowired
    private GoodsHtmlService goodsHtmlService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "E2BUY.ITEM.SAVE.QUEUE",durable ="true"),
            exchange = @Exchange(value = "E2BUY.ITEM.EXCHANGE",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            key = {"item.insert","item.update"}
    ))
    public void save(Long id){
        if(id==null){
            return;
        }
        this.goodsHtmlService.createHtml(id);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "E2BUY.ITEM.DELETE.QUEUE",durable = "true"),
            exchange = @Exchange(value = "E2BUY.ITEM.EXCHANGE",ignoreDeclarationExceptions ="true",type = ExchangeTypes.TOPIC),
            key = {"item.delete"}
    ))
    public void delete(Long id){
        if(id==null){
            return;
        }
        this.goodsHtmlService.deleteHtml(id);
    }
}
