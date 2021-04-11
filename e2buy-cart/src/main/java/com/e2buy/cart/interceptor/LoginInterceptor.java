package com.e2buy.cart.interceptor;

import com.e2buy.cart.config.JwtProperties;
import com.e2buy.common.pojo.UserInfo;
import com.e2buy.common.utils.CookieUtils;
import com.e2buy.common.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: zhangjianwu
 * @Date: 2021/4/11 16:31
 * @Desc: springmvc的拦截器
 **/
@EnableConfigurationProperties(JwtProperties.class)
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

    private static final ThreadLocal<UserInfo> THREAD_LOCAL=new ThreadLocal<>();

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取cookie中的token
        String token = CookieUtils.getCookieValue(request, this.jwtProperties.getCookieName());
        //解析token，获取用户信息
        UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
        if(userInfo==null){
            return false;
        }
        //把userInfo放入线程局部变量
        THREAD_LOCAL.set(userInfo);
        return true;
    }
   //返回userInfo
    public static UserInfo getUserInfo(){
       return THREAD_LOCAL.get();
    }

    //重写完成方法,释放资源
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清空线程的局部变量，因为使用的是tomcat的线程池，线程不会结束，也就不会释放线程的局部变量,需要手动清空
        THREAD_LOCAL.remove();
    }
}
