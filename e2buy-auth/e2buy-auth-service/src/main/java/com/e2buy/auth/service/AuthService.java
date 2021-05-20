package com.e2buy.auth.service;

import com.e2buy.auth.client.UserClient;
import com.e2buy.auth.config.JwtProperties;
import com.e2buy.common.pojo.UserInfo;
import com.e2buy.common.utils.JwtUtils;
import com.e2buy.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: zhangjianwu
 * @Date: 2021/4/5 0:05
 * @Desc:
 **/
@Service
public class AuthService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 前台验证
     * @param username
     * @param password
     * @return
     */
    public String accredit(String username, String password) {
        //根据username password获取user
        User user = userClient.queryUser(username, password);
        //判断user
        if(user==null){
            return null;
        }
        try{
            // jwtUtils生成Jwt类型的token
            UserInfo userInfo = new UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            return JwtUtils.generateToken(userInfo,jwtProperties.getPrivateKey(), jwtProperties.getExpire());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 后台验证
     * @param username
     * @param password
     * @return
     */
    public String accreditBack(String username, String password) {
        //根据username password获取user
        User user = userClient.queryUser(username, password);
        if(user.getRole()==0){
            return null;
        }
        //判断user
        if(user==null){
            return null;
        }
        try{
            // jwtUtils生成Jwt类型的token
            UserInfo userInfo = new UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            return JwtUtils.generateToken(userInfo,jwtProperties.getPrivateKey(), jwtProperties.getExpire());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
