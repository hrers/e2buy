package com.e2buy.cart.service;

import com.e2buy.cart.client.GoodsClient;
import com.e2buy.cart.interceptor.LoginInterceptor;
import com.e2buy.cart.pojo.Cart;
import com.e2buy.common.pojo.UserInfo;
import com.e2buy.common.utils.JsonUtils;
import com.e2buy.item.pojo.Sku;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: zhangjianwu
 * @Date: 2021/4/11 17:20
 * @Desc:
 **/
@Service
public class CartService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private GoodsClient goodsClient;

    private static final String KEY_PREFIX="user:cart:";

    public void addCart(Cart cart) {
        //获取用户信息
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //查询购物车记录 key Object,value Object ,String KEY_PREFIX
        BoundHashOperations<String, Object, Object> hashOperations= this.stringRedisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());
        //判断当前的商品是否在购物车中
        Integer num = cart.getNum();
        String key=cart.getSkuId().toString();
        if(hashOperations.hasKey(cart.getSkuId().toString())){
            //在，更新数量
            String cartJson = hashOperations.get(key).toString();
            cart= JsonUtils.parse(cartJson, Cart.class);
            cart.setNum(cart.getNum()+num);
        }else {
            //不在，新增购物车
            Sku sku = goodsClient.querySkuBySkuId(cart.getSkuId());
            cart.setUserId(userInfo.getId());
            cart.setTitle(sku.getTitle());
            cart.setOwnSpec(sku.getOwnSpec());
            cart.setImage(StringUtils.isBlank(sku.getImages())?"":StringUtils.split(sku.getImages(),",")[0]);
            cart.setPrice(sku.getPrice());
        }
        //序列化cart进入redis
        hashOperations.put(key,JsonUtils.serialize(cart));
    }

    public List<Cart> queryCartList(){
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //判断用户是否有购物车记录
        if(stringRedisTemplate.hasKey(KEY_PREFIX+userInfo.getId())){
            //获取用户的购物车记录
            BoundHashOperations<String, Object, Object> hashOperations = stringRedisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());
            //获取购物车Map中的所有cart值
            List<Object> cartsJson= hashOperations.values();
            //如果购物车为空
            if(CollectionUtils.isEmpty(cartsJson)){
                return null;
            }
            //把List<Object>集合转化为List<Cart>集合
           return cartsJson.stream().map(cartJson->{
                return JsonUtils.parse(cartJson.toString(),Cart.class);
            }).collect(Collectors.toList());
        }else{
            return null;
        }
    }

    /**
     * 更新购物车商品信息
     * @param cart
     */
    public void updateNum(Cart cart) {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //判断用户是否有购物车记录
        if(!stringRedisTemplate.hasKey(KEY_PREFIX+userInfo.getId())){
           return;
        }
        Integer num = cart.getNum();
        //获取用户购物车记录
        BoundHashOperations<String, Object, Object> hashOperations = stringRedisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());
        String cartJson = hashOperations.get(cart.getSkuId().toString()).toString();
        cart= JsonUtils.parse(cartJson, Cart.class);
        cart.setNum(num);
        hashOperations.put(cart.getSkuId().toString(),JsonUtils.serialize(cart));
    }

    public void deleteCart(String skuId) {
        // 获取登录用户
        UserInfo user = LoginInterceptor.getUserInfo();
        String key = KEY_PREFIX + user.getId();
        BoundHashOperations<String, Object, Object> hashOps = this.stringRedisTemplate.boundHashOps(key);
        hashOps.delete(skuId);
    }
}
