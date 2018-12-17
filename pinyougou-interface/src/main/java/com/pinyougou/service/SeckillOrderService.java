package com.pinyougou.service;

import com.pinyougou.pojo.SeckillOrder;

import java.util.List;
import java.io.Serializable;

/**
 * 秒杀商品订单服务接口
 *
 * @version 1.0
 * @date 2018-11-18 21:04:19
 */
public interface SeckillOrderService {

    /**
     * 添加方法
     */
    void save(SeckillOrder seckillOrder);

    /**
     * 修改方法
     */
    void update(SeckillOrder seckillOrder);

    /**
     * 根据主键id删除
     */
    void delete(Serializable id);

    /**
     * 批量删除
     */
    void deleteAll(Serializable[] ids);

    /**
     * 根据主键id查询
     */
    SeckillOrder findOne(Serializable id);

    /**
     * 查询全部
     */
    List<SeckillOrder> findAll();

    /**
     * 多条件分页查询
     */
    List<SeckillOrder> findByPage(SeckillOrder seckillOrder, int page, int rows);

    /**
     * 提交订单到Redis
     *
     * @param id     秒杀商品id
     * @param userId 用户id
     */
    void submitOrderToRedis(Long id, String userId);

    /**
     * 根据用户名查询秒杀订单
     *
     * @param userId 用户名
     * @retrun SeckillOrder
     */
    SeckillOrder findOrderFromRedis(String userId);

    /**
     * 检测支付状态
     *
     * @param userId        用户名
     * @param transactionId 微信交易流水号
     */
    void saveOrder(String userId, String transactionId);



    /**
     * 查询超时5分钟还未支付的订单
     * @return List<SeckillOrder>
     */
    List<SeckillOrder> findOrderByTimeout();


    /**
     * 删除超时未支付订单
     * @param seckillOrder
     */
    void deleteOrderFromRedis(SeckillOrder seckillOrder);
}