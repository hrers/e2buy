package com.e2buy.user.service;

import com.e2buy.user.pojo.User;

/**
 * @Author: zhangjianwu
 * @Date: 2021/3/28 22:49
 * @Desc:
 **/
public interface UserService {

    Boolean checkUser(String data, Integer type);

    Boolean sendVerifyCode(String phone);

    Boolean register(User user, String code);

    User queryUser(String username, String password);

    void changePassword(String username,Long userId, String newPassword);
}
