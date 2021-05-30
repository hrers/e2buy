package com.e2buy.order.controller;

import com.e2buy.common.pojo.PageResult;
import com.e2buy.dto.SaleResult;
import com.e2buy.order.pojo.Order;
import com.e2buy.order.service.OrderService;
import com.e2buy.utils.PayHelper;
import com.e2buy.utils.PayState;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("order")
@Api("订单服务接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PayHelper payHelper;


    @PostMapping("test")
    public String test(){
        System.out.println("hello this is test");
        return "hello test";
    }

    /**
     * 创建订单
     *
     * @param order 订单对象
     * @return 订单编号
     */
    @PostMapping
    @ApiOperation(value = "创建订单接口，返回订单编号", notes = "创建订单")
    @ApiImplicitParam(name = "order", required = true, value = "订单的json对象,包含订单条目和物流信息")
    public ResponseEntity<Long> createOrder(@RequestBody @Valid Order order) {
        Long id = null;
        try {
            id = this.orderService.createOrder(order);
        } catch (Exception e) {
            return new ResponseEntity("下单商品数量已经超过了库存量",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    /**
     * 根据订单编号查询订单
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @ApiOperation(value = "根据订单编号查询订单，返回订单对象", notes = "查询订单")
    @ApiImplicitParam(name = "id", required = true, value = "订单的编号")
    public ResponseEntity<Order> queryOrderById(@PathVariable("id") Long id) {
        Order order = this.orderService.queryById(id);
        if (order == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(order);
    }

    /**
     * 分页查询当前用户订单
     *
     * @param status 订单状态
     * @return 分页订单数据
     */
    @GetMapping("list")
    @ApiOperation(value = "分页查询当前用户订单，并且可以根据订单状态过滤", notes = "分页查询当前用户订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", defaultValue = "1", type = "Integer"),
            @ApiImplicitParam(name = "rows", value = "每页大小", defaultValue = "5", type = "Integer"),
            @ApiImplicitParam(name = "status", value = "订单状态：1未付款，2已付款未发货，3已发货未确认，4已确认未评价，5交易关闭，6交易成功，已评价", type = "Integer"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "订单的分页结果"),
            @ApiResponse(code = 404, message = "没有查询到结果"),
            @ApiResponse(code = 500, message = "查询失败"),
    })
    public ResponseEntity<PageResult<Order>> queryUserOrderList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "status", required = false) Integer status) {
        PageResult<Order> result = this.orderService.queryUserOrderList(page, rows, status);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }


    //todo 查询所有订单信息（商家接口)
    /**
     *
     * @param page
     * @param rows
     * @param status
     * @return
     */
    @GetMapping("list/back")
    public ResponseEntity<PageResult<Order>> queryUserOrderListByAdmin(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "status", required = false) Integer status) {
        PageResult<Order> result = this.orderService.queryUserOrderListBack(page, rows, status);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }


    /**
     * 更新订单状态
     *
     * @param id
     * @param status
     * @return
     */
    @PutMapping("{id}/{status}")
    @ApiOperation(value = "更新订单状态", notes = "更新订单状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订单编号", type = "Long"),
            @ApiImplicitParam(name = "status", value = "订单状态：1未付款，2已付款未发货，3已发货未确认，4已确认未评价，5交易关闭，6交易成功，已评价", type = "Integer"),
    })

    @ApiResponses({
            @ApiResponse(code = 204, message = "true：修改状态成功；false：修改状态失败"),
            @ApiResponse(code = 400, message = "请求参数有误"),
            @ApiResponse(code = 500, message = "查询失败")
    })
    public ResponseEntity<Boolean> updateStatus(@PathVariable("id") Long id, @PathVariable("status") Integer status) {
        Boolean boo = this.orderService.updateStatus(id, status);
        if (boo == null) {
            // 返回400
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // 返回204
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 生成付款链接
     *
     * @param orderId
     * @return
     */
    @GetMapping("url/{id}")
    @ApiOperation(value = "生成微信扫码支付付款链接", notes = "生成付款链接")
    @ApiImplicitParam(name = "id", value = "订单编号", type = "Long")
    @ApiResponses({
            @ApiResponse(code = 200, message = "根据订单编号生成的微信支付地址"),
            @ApiResponse(code = 404, message = "生成链接失败"),
            @ApiResponse(code = 500, message = "服务器异常"),
    })
    public ResponseEntity<String> generateUrl(@PathVariable("id") Long orderId) {
        // 生成付款链接
        String url = this.payHelper.createPayUrl(orderId);
        if (StringUtils.isBlank(url)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(url);
    }

    /**
     * 查询付款状态
     *
     * @param orderId
     * @return 0, 状态查询失败 1,支付成功 2,支付失败
     */
    @GetMapping("state/{id}")
    @ApiOperation(value = "查询扫码支付付款状态", notes = "查询付款状态")
    @ApiImplicitParam(name = "id", value = "订单编号", type = "Long")
    @ApiResponses({
            @ApiResponse(code = 200, message = "0, 未查询到支付信息 1,支付成功 2,支付失败"),
            @ApiResponse(code = 500, message = "服务器异常"),
    })
    public ResponseEntity<Integer> queryPayState(@PathVariable("id") Long orderId) {
        PayState payState = this.payHelper.queryOrder(orderId);
        return ResponseEntity.ok(payState.getValue());
    }

    /**
     * 根据订单编号删除 订单信息
     * @param orderId
     * @return
     */
    @DeleteMapping("delete/{id}")
    @ApiOperation(value = "根据订单id删除订单",notes = "删除订单")
    @ApiImplicitParam(name = "id",value = "订单编号",type = "Long")
    public ResponseEntity<Void> deleteOrderById(@PathVariable("id") Long orderId){
        orderService.deleteOrderById(orderId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("getSaleResult")
    public ResponseEntity<SaleResult> getSaleResult(){
        SaleResult saleResult=orderService.getSaleResult();
        return ResponseEntity.ok(saleResult);
    }

    /**
     * 商家根据订单编号取消订单信息
     * todo 暂时和调用用户的接口，后续可以做区别处理
     */
    @DeleteMapping("admin/cancelOrder/{orderId}")
    public ResponseEntity<Void> adminCancelOrderById(@PathVariable("orderId") Long orderId){
       orderService.deleteOrderById(orderId);
       return ResponseEntity.ok().build();
    }

    /**
     * 用户申请退款
     * @param orderId
     * @return
     */
    @PutMapping("requestCancel/{orderId}")
    public ResponseEntity<Void> requestCancelOrderByOrderId(@PathVariable("orderId") Long orderId){
        orderService.requestCancelOrderByOrderId(orderId);
        return ResponseEntity.ok().build();
    }

    /**
     * 取消退款申请,将订单转为已付款待发货状态
     * @param orderId
     * @return
     */
    @PutMapping("continue/{orderId}")
    public ResponseEntity<Void> continueOrderByOrderId(@PathVariable("orderId")Long orderId){
        orderService.continueOrderByOrderId(orderId);
        return ResponseEntity.ok().build();
    }



    /**
     * 商家确认发货
     */
    @PutMapping("admin/sendOrder/{orderId}")
    public ResponseEntity<Void> adminSendOrderByOrderId(@PathVariable("orderId") Long orderId){
       orderService.sendOrderByOrderId(orderId);
       return ResponseEntity.ok().build();
    }

    /**
     * 用户确认收货
     */
    @PutMapping("/confirmReceived/{orderId}")
    public ResponseEntity<Void> confirmReceivedByOrderId(@PathVariable("orderId")Long orderId){
        orderService.confirmReceived(orderId);
        return ResponseEntity.ok().build();
    }


    /**
     * 获取已销售数量
     * @param skuId
     * @return
     */
    @GetMapping("saledNumBySku/{skuId}")
    public ResponseEntity<String> querySaledNumBySku(@PathVariable("skuId")Long skuId){
        Integer intNum=this.orderService.querySaledNumBySkuId(skuId);
        String strNum="";
        if(intNum==null){
           strNum="0";
        }else {
            strNum=intNum.toString();
        }
        return ResponseEntity.ok(strNum);
    }







}
