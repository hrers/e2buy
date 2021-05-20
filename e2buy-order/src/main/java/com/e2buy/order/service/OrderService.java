package com.e2buy.order.service;

import com.e2buy.common.error.BusinessException;
import com.e2buy.common.pojo.PageResult;
import com.e2buy.common.pojo.UserInfo;
import com.e2buy.common.utils.IdWorker;
import com.e2buy.dto.SaleResult;
import com.e2buy.item.pojo.Stock;
import com.e2buy.order.interceptor.LoginInterceptor;
import com.e2buy.order.mapper.OrderDetailMapper;
import com.e2buy.order.mapper.OrderMapper;
import com.e2buy.order.mapper.OrderStatusMapper;
import com.e2buy.order.mapper.StockMapper;
import com.e2buy.order.pojo.Order;
import com.e2buy.order.pojo.OrderDetail;
import com.e2buy.order.pojo.OrderStatus;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper detailMapper;

    @Autowired
    private OrderStatusMapper statusMapper;

    @Autowired
    private StockMapper stockMapper;

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Transactional
    public Long createOrder(Order order) throws Exception {
        // 生成orderId
        long orderId = idWorker.nextId();
        // 获取登录用户
        UserInfo user = LoginInterceptor.getLoginUser();
        // 初始化数据
        order.setBuyerNick(user.getUsername());
        order.setBuyerRate(false);
        order.setCreateTime(new Date());
        order.setOrderId(orderId);
        order.setUserId(user.getId());
        // 保存数据
        this.orderMapper.insertSelective(order);
        // 保存订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setCreateTime(order.getCreateTime());
        orderStatus.setStatus(1);// 初始状态为未付款

        this.statusMapper.insertSelective(orderStatus);
        // 订单详情中添加orderId
        order.getOrderDetails().forEach(od -> od.setOrderId(orderId));
        // 保存订单详情,使用批量插入功能
        this.detailMapper.insertList(order.getOrderDetails());

        logger.debug("生成订单，订单编号：{}，用户id：{}", orderId, user.getId());
        //减库存
        //order.getOrderDetails().forEach(orderDetail -> this.stockMapper.reduceStock(orderDetail.getSkuId(), orderDetail.getNum()));
        order.getOrderDetails().forEach(orderDetail -> {
            //判断减后商品是否小于0
            Stock example= new Stock();
            example.setSkuId(orderDetail.getSkuId());
            Stock stock= stockMapper.selectOne(example);
            //如果下单后商品的库存量大于0那么可以下单,生成订单商品数量就开始减了
            if(stock.getStock()-orderDetail.getNum()>0) {
                this.stockMapper.reduceStock(orderDetail.getSkuId(), orderDetail.getNum());
            }else {
                throw new BusinessException(orderDetail.getTitle()+"数量超过了库存量");
            }
        });
        return orderId;
    }

    public Order queryById(Long id) {
        // 查询订单
        Order order = this.orderMapper.selectByPrimaryKey(id);

        // 查询订单详情
        OrderDetail detail = new OrderDetail();
        detail.setOrderId(id);
        List<OrderDetail> details = this.detailMapper.select(detail);
        order.setOrderDetails(details);

        // 查询订单状态
        OrderStatus status = this.statusMapper.selectByPrimaryKey(order.getOrderId());
        order.setStatus(status.getStatus());
        return order;
    }

    /**
     * 买家订单信息
     * @param page
     * @param rows
     * @param status
     * @return
     */
    public PageResult<Order> queryUserOrderList(Integer page, Integer rows, Integer status) {
        try {
            // 分页
            PageHelper.startPage(page, rows);
            // 获取登录用户
            UserInfo user = LoginInterceptor.getLoginUser();
            // 创建查询条件
            Page<Order> pageInfo = (Page<Order>) this.orderMapper.queryOrderList(user.getId(), status);

            return new PageResult<>(pageInfo.getTotal(), pageInfo);
        } catch (Exception e) {
            logger.error("查询订单出错", e);
            return null;
        }
    }

    /**
     * 商家订单信息接口
     * @param page
     * @param rows
     * @param status
     * @return
     */
    public PageResult<Order> queryUserOrderListBack(Integer page, Integer rows, Integer status) {
        try {
            // 分页
            PageHelper.startPage(page, rows);
            // 获取登录用户
            UserInfo user = LoginInterceptor.getLoginUser();
            // 创建查询条件
            Page<Order> pageInfo = (Page<Order>) this.orderMapper.queryOrderListBack(status);

            return new PageResult<>(pageInfo.getTotal(), pageInfo);
        } catch (Exception e) {
            logger.error("查询订单出错", e);
            return null;
        }
    }



    @Transactional
    public Boolean updateStatus(Long id, Integer status) {
        OrderStatus record = new OrderStatus();
        record.setOrderId(id);
        record.setStatus(status);
        // 根据状态判断要修改的时间
        switch (status) {
            case 2:
                record.setPaymentTime(new Date());// 付款
                break;
            case 3:
                record.setConsignTime(new Date());// 发货
                break;
            case 4:
                record.setEndTime(new Date());// 确认收获，订单结束
                break;
            case 5:
                record.setCloseTime(new Date());// 交易失败，订单关闭
                break;
            case 6:
                record.setCommentTime(new Date());// 评价时间
                break;
            default:
                return null;
        }
        int count = this.statusMapper.updateByPrimaryKeySelective(record);
        return count == 1;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteOrderById(Long orderId) {
        //删除订单表
        orderMapper.deleteByPrimaryKey(orderId);
        //查询orderDetail表,将相应的sku数量减掉
        OrderDetail detail = new OrderDetail();
        detail.setOrderId(orderId);
        List<OrderDetail> details = this.detailMapper.select(detail);
        //取消订单后需要将商品数量恢复回来
        details.forEach(orderDetail -> {
            this.stockMapper.increaseStock(orderDetail.getSkuId(),orderDetail.getNum());
        });

        Example detailExample = new Example(OrderDetail.class);
        detailExample.createCriteria().andEqualTo("orderId",orderId);
        //删除订单细节表
        detailMapper.deleteByExample(detailExample);
        Example statusExample = new Example(OrderStatus.class);
        statusExample.createCriteria().andEqualTo("orderId",orderId);
        //删除该订单的状态表
        statusMapper.deleteByExample(statusExample);
    }

    public SaleResult getSaleResult() {
        /**
         * SELECT sum(actual_pay)
         * FROM tb_order
         *  WHERE to_days(create_time) = to_days(now());
         */
        List<Order> orders = orderMapper.selectAll();
        int day = new Date().getDay();

        Calendar calendar = Calendar.getInstance();
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.setTime(orders.get(0).getCreateTime());

        //今天 总的
        long todayMoney= orders.stream().filter(o->o.getCreateTime().getDay()==day).mapToLong(Order::getActualPay).sum();
        long totalMoney= orders.stream().mapToLong(Order::getActualPay).sum();
        SaleResult saleResult = new SaleResult();

        //获取过去几天
        Long[] sales = new Long[5];
        for (int i = 0; i < sales.length; i++) {
           sales[i]=0L;
        }
        orders.forEach(order -> {
          int d= Integer.parseInt(order.getCreateTime().toString().split(" ")[2]);
          switch (dayOfMonth-d){
              case 0:
                  sales[0]+=order.getActualPay();
                  break;
              case 1:
                  sales[1]+=order.getActualPay();
                  break;
              case 2:
                  sales[2]+=order.getActualPay();
                  break;
              case 3:
                  sales[3]+=order.getActualPay();
                  break;
              default:
                  sales[4]+=order.getActualPay();
                  break;
          }
        });
        saleResult.setTodayMoney(todayMoney);
        saleResult.setTotalMoney(totalMoney);
        saleResult.setSales(sales);
        return  saleResult;
    }

    @Test
    public void test(){
//        Calendar calendar= Calendar.getInstance();
//        int dayofMonth = calendar.get(Calendar.DAY_OF_MONTH);
//        System.out.println(dayofMonth);

    }
}
