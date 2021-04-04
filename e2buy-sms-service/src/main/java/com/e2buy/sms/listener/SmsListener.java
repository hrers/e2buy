package com.e2buy.sms.listener;

import com.e2buy.sms.config.SmsProperties;
import com.e2buy.sms.util.SmsUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author: zhangjianwu
 * @Date: 2021/3/29 23:24
 * @Desc:
 **/
@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SmsListener {

    @Autowired
    private SmsUtils smsUtils;

    @Autowired
    private SmsProperties smsProperties;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "e2buy.sms.queue",durable = "true"),
            exchange = @Exchange(value = "e2buy.sms.exchange"),
            ignoreDeclarationExceptions = ("true"),
            key = {"sms.verify.code"}
    ))
    public void listenSms(Map<String,String> msg) throws Exception{
        if( msg == null || msg.size()<=0){
            return;
        }
        String phone = msg.get("phone");
        String code = msg.get("code");

        if(StringUtils.isBlank(phone) || StringUtils.isBlank(code)){
           return;
        }
        smsUtils.send(phone,code);
    }
}
