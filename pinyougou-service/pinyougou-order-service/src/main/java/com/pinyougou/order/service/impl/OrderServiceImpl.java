package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.Cart;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.OrderItemMapper;
import com.pinyougou.mapper.OrderMapper;
import com.pinyougou.mapper.PayLogMapper;
import com.pinyougou.pojo.Order;
import com.pinyougou.pojo.OrderItem;
import com.pinyougou.pojo.PayLog;
import com.pinyougou.service.OrderService;
import com.pinyougou.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author NTP
 * @date 2018/12/15
 */
@Service(interfaceName = "com.pinyougou.service.OrderService")
@Transactional
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private PayLogMapper payLogMapper;

    /**
     * 保存订单
     *
     * @param order
     */
    @Override
    public void save(Order order) {
        try {
            // 根据用户名获取Redis中购物车数据
            List<Cart> carts = (List<Cart>) redisTemplate.boundValueOps("cart_" + order.getUserId()).get();

            //定义订单ID集合(一次支付对应多个订单)
            //List<String> orderIdList = new ArrayList<>();
            //定义订单号
            StringBuilder orderIds = new StringBuilder();
            /** 定义多个订单支付的总金额（元） */
            double totalMoney = 0;

            // 迭代购物车数据
            for (Cart cart : carts) {
                /** ####### 往订单表插入数据 ######### */
                // 生成订单主键id
                long orderId = idWorker.nextId();
                // 创建新的订单
                Order order1 = new Order();
                // 设置订单id
                order1.setOrderId(orderId);
                // 设置支付类型
                order1.setPaymentType(order.getPaymentType());
                // 设置支付状态码为“未支付”
                order1.setStatus("1");
                // 设置订单创建时间
                order1.setCreateTime(new Date());
                // 设置订单修改时间
                order1.setUpdateTime(order1.getCreateTime());
                // 设置用户名
                order1.setUserId(order.getUserId());
                // 设置收件人地址
                order1.setReceiverAreaName(order.getReceiverAreaName());
                // 设置收件人手机号码
                order1.setReceiverMobile(order.getReceiverMobile());
                // 设置收件人
                order1.setReceiver(order.getReceiver());
                // 设置订单来源
                order1.setSourceType(order.getSourceType());
                // 设置商家id
                order1.setSellerId(cart.getSellerId());

                // 定义该订单总金额
                double money = 0;
                /** ####### 往订单明细表插入数据 ######### */
                for (OrderItem orderItem : cart.getOrderItems()) {
                    // 设置主键id
                    orderItem.setId(idWorker.nextId());
                    // 设置关联的订单id
                    orderItem.setOrderId(orderId);
                    // 累计总金额
                    money += orderItem.getTotalFee().doubleValue();
                    // 保存数据到订单明细表
                    orderItemMapper.insertSelective(orderItem);
                }
                // 设置支付总金额
                order1.setPayment(new BigDecimal(money));
                // 保存数据到订单表
                orderMapper.insertSelective(order1);

                //记录订单id
                //orderIdList.add(String.valueOf(orderId));
                /** 记录总金额 */
                totalMoney += money;
                //拼接多个订单号
                orderIds.append(orderId + ",");
            }

            /** 判断是否为微信支付 */
            if ("1".equals(order.getPaymentType())) {
                /** 创建支付日志对象 */
                PayLog payLog = new PayLog();
                /** 设置订单交易号 */
                payLog.setOutTradeNo(String.valueOf(idWorker.nextId()));
                /** 创建时间 */
                payLog.setCreateTime(new Date());
                /** 支付总金额(分)，多个订单的 */
                payLog.setTotalFee((long) (totalMoney * 100));
                /** 用户ID */
                payLog.setUserId(order.getUserId());
                /** 支付状态 */
                payLog.setTradeState("0");
                //订单号集合，逗号分隔
                //String ids = orderIdList.toString().replace("[", "").replace("]", "").replace(" ","");
                /** 设置订单号 */
                payLog.setOrderList(orderIds.toString().substring(0, orderIds.toString().length() - 1));
                /** 支付类型 */
                payLog.setPayType(order.getPaymentType());
                /** 往支付日志表插入数据 */
                payLogMapper.insertSelective(payLog);
                /** 把支付日志对象存入redis数据库(方便生成支付二维码) */
                redisTemplate.boundValueOps("payLog_" + order.getUserId()).set(payLog);
            }

            // 删除该用户购物车数据
            redisTemplate.delete("cart_" + order.getUserId());

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 修改方法
     *
     * @param order
     */
    @Override
    public void update(Order order) {

    }

    /**
     * 根据主键id删除
     *
     * @param id
     */
    @Override
    public void delete(Serializable id) {

    }

    /**
     * 批量删除
     *
     * @param ids
     */
    @Override
    public void deleteAll(Serializable[] ids) {

    }

    /**
     * 根据主键id查询
     *
     * @param id
     */
    @Override
    public Order findOne(Serializable id) {
        return null;
    }

    /**
     * 查询全部
     */
    @Override
    public List<Order> findAll() {
        return null;
    }

    /**
     * 多条件分页查询
     *
     * @param order
     * @param page
     * @param rows
     */
    @Override
    public List<Order> findByPage(Order order, int page, int rows) {
        return null;
    }

    /**
     * 从Redis查询用户最新的支付日志对象
     *
     * @param userId
     * @return PayLog
     */
    @Override
    public PayLog findPayLogFromRedis(String userId) {
        try {
            return (PayLog) redisTemplate.boundValueOps("payLog_" + userId).get();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 修改订单状态
     *
     * @param outTradeNo    订单交易号
     * @param transactionId 微信交易流水号
     */
    @Override
    public void updateOrderStatus(String outTradeNo, String transactionId) {
        try {
            /** 修改支付日志状态 */
            PayLog payLog = payLogMapper.selectByPrimaryKey(outTradeNo);
            payLog.setPayTime(new Date());
            payLog.setTradeState("1"); // 已支付
            payLog.setTransactionId(transactionId);// 交易流水号
            payLogMapper.updateByPrimaryKeySelective(payLog);

            /** 修改订单状态 */
            String[] orderIds = payLog.getOrderList().split(","); // 订单号列表
            /** 循环订单号数组 */
            for (String orderId : orderIds) {
                Order order = new Order();
                order.setOrderId(Long.valueOf(orderId));
                order.setPaymentTime(new Date()); // 支付时间
                order.setStatus("2"); // 已付款
                orderMapper.updateByPrimaryKeySelective(order);
            }
            /** 清除redis缓存数据 */
            redisTemplate.delete("payLog_" + payLog.getUserId());

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
