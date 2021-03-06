package com.e2buy.auth.controller;

import com.e2buy.auth.config.JwtProperties;
import com.e2buy.auth.service.AuthService;
import com.e2buy.common.pojo.UserInfo;
import com.e2buy.common.utils.CookieUtils;
import com.e2buy.common.utils.JwtUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: zhangjianwu
 * @Date: 2021/4/5 0:03
 * @Desc:
 **/
@Controller
@EnableConfigurationProperties(JwtProperties.class)//启用JwtProperties类
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/accredit")
    public ResponseEntity<Void> accredit(@RequestParam("username") String username,
                                         @RequestParam("password")String password,
                                         HttpServletResponse response,
                                         HttpServletRequest request){
        String token=authService.accredit(username,password);
        if(StringUtils.isBlank(token)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        CookieUtils.setCookie(request,response,this.jwtProperties.getCookieName(),token,this.jwtProperties.getExpire()*60);//以秒为单位
        return ResponseEntity.ok(null);
    }

    /**
     * 后台验证
     * @param username
     * @param password
     * @param response
     * @param request
     * @return
     */
    @PostMapping("/accredit/back")
    public ResponseEntity<Void> accreditBack(@RequestParam("username") String username,
                                         @RequestParam("password")String password,
                                         HttpServletResponse response,
                                         HttpServletRequest request){
        String token=authService.accreditBack(username,password);
        if(StringUtils.isBlank(token)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        CookieUtils.setCookie(request,response,this.jwtProperties.getCookieName(),token,this.jwtProperties.getExpire()*60);//以秒为单位
        return ResponseEntity.ok(null);
    }


    @GetMapping("verify")
    public ResponseEntity<UserInfo> verify(@CookieValue("E2BUY_TOKEN")String token
            ,HttpServletRequest request,HttpServletResponse response) {
        UserInfo userInfo= null;
        try {
            userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            if(userInfo==null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            //刷新jwt中的有效时间
            token = JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getExpire());

            //刷新cookie中的有效时间
            CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),token,jwtProperties.getExpire()*60);
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


}
