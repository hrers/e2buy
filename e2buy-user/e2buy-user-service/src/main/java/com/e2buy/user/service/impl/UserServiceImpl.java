package com.e2buy.user.service.impl;

import com.e2buy.user.enums.CheckTypeEnum;
import com.e2buy.user.mapper.UserMapper;
import com.e2buy.user.pojo.User;
import com.e2buy.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: zhangjianwu
 * @Date: 2021/3/28 22:50
 * @Desc:
 **/
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;


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
}
