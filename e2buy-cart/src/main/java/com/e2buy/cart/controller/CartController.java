package com.e2buy.cart.controller;

import com.e2buy.cart.pojo.Cart;
import com.e2buy.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: zhangjianwu
 * @Date: 2021/4/11 17:20
 * @Desc:
 **/
@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    //网关那里已经定义了 "/cart"的全局路径
    /**
     *  加入购物车
     * @param cart
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart){
       cartService.addCart(cart);
       return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    /**
     * 查询购物车列表
     *
     * @return
     */
    @GetMapping
    public ResponseEntity<List<Cart>> queryCartList() {
        List<Cart> carts = this.cartService.queryCartList();
        if (carts == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(carts);
    }

    /**
     * 更新购物车商品数量
     * @param cart
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateNum(@RequestBody Cart cart){
       cartService.updateNum(cart);
       return ResponseEntity.noContent().build();
    }

    /**
     * 删除购物车商品
     * @param skuId
     * @return
     */
    @DeleteMapping("/{skuId}")
    public ResponseEntity<Void> deleteCart(@PathVariable("skuId") String skuId) {
        System.out.println("test");
        this.cartService.deleteCart(skuId);
        return ResponseEntity.ok().build();
    }


}
