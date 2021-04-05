package com.e2buy.user.api;

import com.e2buy.user.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: zhangjianwu
 * @Date: 2021/4/5 0:43
 * @Desc:
 **/
@RequestMapping("user")
public interface UserApi {

    @GetMapping("query")
    public User queryUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password);
}