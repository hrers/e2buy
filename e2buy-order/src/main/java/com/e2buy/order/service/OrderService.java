package com.e2buy.order.service;

import com.e2buy.common.error.BusinessException;
import com.e2buy.common.pojo.PageResult;
import com.e2buy.common.pojo.UserInfo;
import com.e2buy.common.utils.IdWorker;
import com.e2buy.dto.SaleResult;
import com.e2buy.item.pojo.SpuDetail;
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
import org.apache.tomcat.jni.Status;
import org.aspectj.weaver.ast.Or;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.awt.*;
import java.text.ParseException;
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
//        List<Order> orders = orderMapper.selectAll();
        //todo 暂时只要已支付(status=2)的订单信息,如果后续加上退货还要处理退货之后的事情
        List<Order> orders=orderMapper.selectWithStatus(2);


        Calendar calendar = Calendar.getInstance();
        int currentDayOfMonth= calendar.get(Calendar.DAY_OF_MONTH);
        int currentDayOfYear= calendar.get(Calendar.DAY_OF_YEAR);
        int currentWeekOfMonth= calendar.get(Calendar.WEEK_OF_MONTH);
        int currentYear=calendar.get(Calendar.YEAR);
        int currentMonth=calendar.get(Calendar.MONTH);//calender返回的月份 一月：0
        SimpleDateFormat sdf =   new SimpleDateFormat( " yyyy-MM-dd HH:mm:ss " );
        //今天销售额
        long todayMoney = orders.stream().filter(o -> {
            Date date = null;
            try {
                date = sdf.parse(sdf.format(o.getCreateTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendar.setTime(date);
            int orderDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
            return currentDayOfYear== orderDayOfYear && calendar.get(Calendar.YEAR) == currentYear;
        }).mapToLong(Order::getActualPay).sum();
        //过去七天
        long toweekMoney = orders.stream().filter(o -> {
            Date date = null;
            try {
                date = sdf.parse(sdf.format(o.getCreateTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendar.setTime(date);
            int orderMonth= calendar.get(Calendar.MONTH);
            int orderWeekOfOrderMonth = calendar.get(Calendar.WEEK_OF_MONTH);
            return orderWeekOfOrderMonth == currentWeekOfMonth &&orderMonth==currentMonth && calendar.get(Calendar.YEAR) == currentYear;
        }).mapToLong(Order::getActualPay).sum();
        //本月销售额
        long tomonthMoney = orders.stream().filter(o -> {
            Date date = null;
            try {
                date = sdf.parse(sdf.format(o.getCreateTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendar.setTime(date);
            int orderMonth = calendar.get(Calendar.MONTH);
            return orderMonth == currentMonth && calendar.get(Calendar.YEAR) == currentYear;
        }).mapToLong(Order::getActualPay).sum();
        //今年年度销售额
        long toyearMoney = orders.stream().filter(o -> {
            Date date = null;
            try {
                date = sdf.parse(sdf.format(o.getCreateTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendar.setTime(date);
            int orderYear = calendar.get(Calendar.YEAR);
            return orderYear== currentYear;
        }).mapToLong(Order::getActualPay).sum();
        //总销售额
        long totalMoney = orders.stream().mapToLong(Order::getActualPay).sum();
        //获取过去几天
        Long[] sales = new Long[5];
        for (int i = 0; i < sales.length; i++) {
            sales[i] = 0L;
        }
        //本年季度统计
        orders.forEach(order -> {
            Date date = null;
            try {
                date = sdf.parse(sdf.format(order.getCreateTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendar.setTime(date);
            if (currentYear == calendar.get(Calendar.YEAR)) {
                if (calendar.get(Calendar.MONTH) >= 0 && calendar.get(Calendar.MONTH) <= 2) {
                    sales[0] += order.getActualPay();
                } else if (calendar.get(Calendar.MONTH) >= 3 && calendar.get(Calendar.MONTH) <= 5) {
                    sales[1] += order.getActualPay();
                } else if (calendar.get(Calendar.MONTH) >= 6 && calendar.get(Calendar.MONTH) <= 8) {
                    sales[2] += order.getActualPay();
                } else if (calendar.get(Calendar.MONTH) >= 9 && calendar.get(Calendar.MONTH) <= 11) {
                    sales[3] += order.getActualPay();
                }
            }
        });

        SaleResult saleResult = new SaleResult();
        saleResult.setTodayMoney(todayMoney);
        saleResult.setToweekMoney(toweekMoney);
        saleResult.setTomonthMoney(tomonthMoney);
        saleResult.setToyearMoney(toyearMoney);
        saleResult.setTotalMoney(totalMoney);
        saleResult.setSales(sales);
        return  saleResult;
    }

    @Test
    public void test(){

        Calendar calendar= Calendar.getInstance();
        int dayofMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int dayofYear= calendar.get(Calendar.DAY_OF_YEAR);
        System.out.println("month");
        System.out.println(calendar.get(Calendar.MONTH));
        System.out.println("month");
        System.out.println(dayofMonth);
        System.out.println(dayofYear);
        SimpleDateFormat sdf =   new SimpleDateFormat( " yyyy-MM-dd HH:mm:ss " );
        Date date = null;
        try {
            date = sdf.parse( " 2021-04-21 00:44:40 " );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("************************");
        String format = sdf.format(date);
        try {
            Date parse = sdf.parse(format);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("orderMonth");
        System.out.println(calendar.get(Calendar.MONTH));
        System.out.println("orderMonth");

        System.out.println(format);
        System.out.println("************************");
        calendar.setTime(date);
        int i = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);
        System.out.println("year"+year);
        System.out.println(i);

    }

    public void sendOrderByOrderId(Long orderId) {
        OrderStatus example= new OrderStatus();
        example.setOrderId(orderId);
        // status:3已发货未确认
        example.setStatus(3);
        statusMapper.updateByPrimaryKeySelective(example);
    }

    public void confirmReceived(Long orderId) {
        OrderStatus example= new OrderStatus();
        example.setOrderId(orderId);
        // status:3已发货未确认
        example.setStatus(4);
        statusMapper.updateByPrimaryKeySelective(example);
    }

    public Integer querySaledNumBySkuId(Long skuId) {
        //todo 已售数量：已经付款的
        Integer num=orderMapper.querySaledSkuNumBySkuId(skuId);
        return num;
    }
}
