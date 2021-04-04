package com.e2buy.user.service.impl;

import com.e2buy.common.utils.CodecUtils;
import com.e2buy.common.utils.NumberUtils;
import com.e2buy.user.enums.CheckTypeEnum;
import com.e2buy.user.mapper.UserMapper;
import com.e2buy.user.pojo.User;
import com.e2buy.user.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zhangjianwu
 * @Date: 2021/3/28 22:50
 * @Desc:
 **/
@Service
public class UserServiceImpl implements UserService {

    static final String KEY_PREFIX = "user:code:phone:";
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    static final Logger logger= LoggerFactory.getLogger(UserService.class);


    /**
     * 校验数据是否可用
     * @param data
     * @param type
     * @return
     */
    @Override
    public Boolean checkUser(String data, Integer type) {
        User record = new User();
        if(type.equals(CheckTypeEnum.USERNAME.getType())){
            record.setUsername(data);
        }else if(type.equals(CheckTypeEnum.PHONE.getType())){
            record.setPhone(data);
        }else {
            return null;
        }
        return userMapper.selectCount(record) == 0;
    }

    @Override
    public Boolean sendVerifyCode(String phone) {
        // 生成验证码
        String code = NumberUtils.generateCode(6);
        try {
            // 发送短信
            Map<String, String> msg = new HashMap<>();
            msg.put("phone", phone);
            msg.put("code", code);
            this.amqpTemplate.convertAndSend("e2buy.sms.exchange", "sms.verify.code", msg);
            // 将code存入redis
            this.redisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);
            return true;
        } catch (Exception e) {
            logger.error("发送短信失败。phone：{}， code：{}", phone, code);
            return false;
        }
    }

    @Override
    public void sendTest() {
        try {
            redisTemplate.opsForValue().set("key1","xxx",5,TimeUnit.MINUTES);
            this.amqpTemplate.convertAndSend("e2buy.sms.exchange", "sms.verify.code", "hhh");
        }catch (Exception e){
            logger.error("发送短信失败。phone：{}， code：{}");
        }

    }

    @Override
    public Boolean register(User user, String code) {
        //获取校验码
        String cacheCode= redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());

        if(!StringUtils.equals(code,cacheCode)){
           return false;
        }
        //生成盐
        String salt= CodecUtils.generateSalt();
        user.setSalt(salt);
        // 对密码加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(), salt));

        // 强制设置不能指定的参数为null
        user.setId(null);
        user.setCreated(new Date());
        // 添加到数据库
        boolean b = this.userMapper.insertSelective(user) == 1;

        if(b){
            // 注册成功，删除redis中的记录
            this.redisTemplate.delete(KEY_PREFIX + user.getPhone());
        }
        return b;
    }
}
