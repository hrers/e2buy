package com.e2buy.user.controller;

import com.e2buy.user.pojo.User;
import com.e2buy.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Author: zhangjianwu
 * @Date: 2021/3/28 22:48
 * @Desc: 用户管理API
 **/
@Controller
@RequestMapping("/user")
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

    /**
     * 发送验证码
     * @param phone
     * @return
     */
    @PostMapping("/code")
    public ResponseEntity<Void> sendVerifyCode(String phone){
        Boolean boo=userService.sendVerifyCode(phone);
        if(boo == null || !boo){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * 用户注册接口
     * @param user
     * @param code
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid User user, @RequestParam("code") String code){
       Boolean boo=userService.register(user,code);
       if(boo==null||!boo){
           return ResponseEntity.status(HttpStatus.CREATED).build();
       }
       return new ResponseEntity<>(HttpStatus.CREATED);
    }


    /**
     * 根据用户名和密码查询用户
     * @param username
     * @param password
     * @return
     */
    @GetMapping("query")
    public ResponseEntity<User> queryUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ) {
        User user = this.userService.queryUser(username, password);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(user);
    }

}
