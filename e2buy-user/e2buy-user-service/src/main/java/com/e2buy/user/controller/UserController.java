package com.e2buy.user.controller;

import com.e2buy.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author: zhangjianwu
 * @Date: 2021/3/28 22:48
 * @Desc:
 **/
@Controller
public class UserController {

    @Autowired
    private UserService userService;


    /**
     * 检查类型
     * 1:用户名
     * 2:手机号
     * @param data
     * @param type
     * @return
     */
    @GetMapping("/check/{data}/{type}")
    @ResponseBody
    public ResponseEntity<Boolean> checkUser(@PathVariable("data")String data,@PathVariable("type")Integer type){
        Boolean bool=userService.checkUser(data,type);
        if(bool==null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(bool);
    }

}
