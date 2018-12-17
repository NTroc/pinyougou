package com.pinyougou.seckill.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.mapper.SeckillOrderMapper;
import com.pinyougou.pojo.SeckillGoods;
import com.pinyougou.pojo.SeckillOrder;
import com.pinyougou.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.List;

/**
 * 秒杀订单服务接口实现类
 *
 * @author NTP
 * @date 2018/12/16
 */
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 添加方法
     *
     * @param seckillOrder
     */
    @Override
    public void save(SeckillOrder seckillOrder) {

    }

    /**
     * 修改方法
     *
     * @param seckillOrder
     */
    @Override
    public void update(SeckillOrder seckillOrder) {

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
    public SeckillOrder findOne(Serializable id) {
        return null;
    }

    /**
     * 查询全部
     */
    @Override
    public List<SeckillOrder> findAll() {
        return null;
    }

    /**
     * 多条件分页查询
     *
     * @param seckillOrder
     * @param page
     * @param rows
     */
    @Override
    public List<SeckillOrder> findByPage(SeckillOrder seckillOrder, int page, int rows) {
        return null;
    }

    /**
     * 提交订单到Redis
     *
     * @param id     秒杀商品id
     * @param userId 用户id
     */
    @Override
    public void submitOrderToRedis(Long id, String userId) {
        try {
            // 从Redis中获取秒杀商品
            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("seckillGoodsList").get(id);

            // 判断秒杀商品是否为空
            if (seckillGoods != null && seckillGoods.getStockCount() > 0) {

                /** ############## 正确的方案 ############ */

                // 1. 采用消息队列（点对点）(减库存的问题)
                // 发送消息(id, userId) MQ服务器

                // 消息消费者
                // a. 从Redis中获取秒杀商品
                // b. 判断秒杀商品库存
                // c. 减库存
                // d. 同步库存数量到数据库
                // e. 产生秒杀订单
                // f. 再把秒杀订单存入Redis

                // 减库存
                seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
                //判断库存
                if (seckillGoods.getStockCount() == 0) {
                    // 把秒杀商品同步到数据库(剩余库存)
                    seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
                    // 从Redis数据库中删除该秒杀商品
                    redisTemplate.boundHashOps("seckillGoodsList").delete(id);
                } else {
                    // 把该商品同步到Redis数据库
                    redisTemplate.boundHashOps("seckillGoodsList").put(id, seckillGoods);
                }

                // 创建秒杀订单
                SeckillOrder seckillOrder = new SeckillOrder();
                // 秒杀订单id
                seckillOrder.setId(idWorker.nextId());
                // 秒杀商品id
                seckillOrder.setSeckillId(id);
                // 秒杀金额
                seckillOrder.setMoney(seckillGoods.getCostPrice());
                // 用户id
                seckillOrder.setUserId(userId);
                // 商家id
                seckillOrder.setSellerId(seckillGoods.getSellerId());
                // 创建时间
                seckillOrder.setCreateTime(new Date());
                // 支付状态码(未支付)
                seckillOrder.setStatus("0");
                // 存储到Redis数据库
                redisTemplate.boundHashOps("seckillOrderList").put(userId, seckillOrder);

            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 根据用户名查询秒杀订单
     *
     * @param userId 用户名
     * @retrun SeckillOrder
     */
    @Override
    public SeckillOrder findOrderFromRedis(String userId) {
        try {
            // 从Redis中查询用户秒杀订单
            return (SeckillOrder) redisTemplate.boundHashOps("seckillOrderList").get(userId);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    /**
     * 检测支付状态
     *
     * @param userId        用户名
     * @param transactionId 微信交易流水号
     */
    @Override
    public void saveOrder(String userId, String transactionId) {
        try {
            /** 根据用户ID从redis中查询秒杀订单 */
            SeckillOrder seckillOrder = findOrderFromRedis(userId);
            /** 判断秒杀订单 */
            if (seckillOrder != null) {
                /** 微信交易流水号 */
                seckillOrder.setTransactionId(transactionId);
                /** 支付时间 */
                seckillOrder.setPayTime(new Date());
                /** 状态码(已付款) */
                seckillOrder.setStatus("1");
                /** 保存到数据库 */
                seckillOrderMapper.insertSelective(seckillOrder);
                /** 删除Redis中的订单 */
                redisTemplate.boundHashOps("seckillOrderList").delete(userId);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }


    /**
     * 查询超时5分钟还未支付的订单
     * @return List<SeckillOrder>
     */
    public List<SeckillOrder> findOrderByTimeout(){
        try{
            // 定义超时5分钟未支付的订单集合
            List<SeckillOrder> seckillOrders = new ArrayList<>();

            // 从Redis数据库中查询全部未支付的订单
            List<SeckillOrder> seckillOrderList = redisTemplate.boundHashOps("seckillOrderList").values();
            // 迭代所有未支付的订单
            for (SeckillOrder seckillOrder : seckillOrderList){
                // 获取当前时间毫秒数 - 5分钟的毫秒数
                long endTime = new Date().getTime() - (5 * 60 * 1000);
                // 判断哪些订单超出5分钟，还未支付
                if (seckillOrder.getCreateTime().getTime() < endTime){
                    seckillOrders.add(seckillOrder);
                }

            }
            return seckillOrders;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * 删除超时未支付订单
     * @param seckillOrder
     */
    public void deleteOrderFromRedis(SeckillOrder seckillOrder){
        try{
            // 1. 从Redis数据库删除超时未支付订单
            redisTemplate.boundHashOps("seckillOrderList")
                    .delete(seckillOrder.getUserId());

            // 2. 增加Redis数据库中秒杀商品的库存
            // 获取秒杀商品
            SeckillGoods seckillGoods = (SeckillGoods)redisTemplate.boundHashOps("seckillGoodsList").get(seckillOrder.getSeckillId());
            // 判断秒杀商品是否为空
            if (seckillGoods != null){
                // 增加库存
                seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);
            }else{ // 秒光了
                // 从数据库表中查询秒杀商品
                seckillGoods = seckillGoodsMapper.selectByPrimaryKey(seckillOrder.getSeckillId());
                seckillGoods.setStockCount(1);
            }
            // 同步到Redis
            redisTemplate.boundHashOps("seckillGoodsList").put(seckillGoods.getId(), seckillGoods);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
