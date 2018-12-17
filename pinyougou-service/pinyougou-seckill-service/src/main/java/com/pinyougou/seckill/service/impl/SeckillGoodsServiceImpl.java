package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.pojo.SeckillGoods;
import com.pinyougou.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 秒杀商品服务接口实现类
 *
 * @author NTP
 * @date 2018/12/16
 */
@Service(interfaceName = "com.pinyougou.service.SeckillGoodsService")
@Transactional
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 添加方法
     *
     * @param seckillGoods
     */
    @Override
    public void save(SeckillGoods seckillGoods) {

    }

    /**
     * 修改方法
     *
     * @param seckillGoods
     */
    @Override
    public void update(SeckillGoods seckillGoods) {

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
    public SeckillGoods findOne(Serializable id) {
        return null;
    }

    /**
     * 查询全部
     */
    @Override
    public List<SeckillGoods> findAll() {
        return null;
    }

    /**
     * 多条件分页查询
     *
     * @param seckillGoods
     * @param page
     * @param rows
     */
    @Override
    public List<SeckillGoods> findByPage(SeckillGoods seckillGoods, int page, int rows) {
        return null;
    }


    /**
     * 查询秒杀的商品集合
     *
     * @return List<SeckillGoods>
     */
    @Override
    public List<SeckillGoods>   findSeckillGoods() {
        try {
            // 定义秒杀商品数据
            List<SeckillGoods> seckillGoodsList;
            try {
                //从redis数据库中获取秒杀商品
                seckillGoodsList = redisTemplate.boundHashOps("seckillGoodsList").values();
                if (seckillGoodsList != null && seckillGoodsList.size() > 0) {
                    System.out.println("==========从redis数据库中获取秒杀商品===========");
                    return seckillGoodsList;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            // SELECT *  FROM tb_seckill_goods WHERE STATUS = 1 AND stock_count > 0
            // AND start_time <= NOW() AND end_time >= NOW()
            //创建示范对象
            Example example = new Example(SeckillGoods.class);
            //创建条件对象
            Example.Criteria criteria = example.createCriteria();
            //审核状态：STATUS = 1;
            criteria.andEqualTo("status", 1);
            // 剩余库存数量大于0 stock_count
            criteria.andGreaterThan("stockCount", 0);
            // 开始时间小于等于当前时间 start_time <= NOW()
            criteria.andLessThanOrEqualTo("startTime", new Date());
            // 结束时间大于等于当前时间 end_time >= NOW()
            criteria.andGreaterThanOrEqualTo("endTime", new Date());
            //条件查询
            seckillGoodsList = seckillGoodsMapper.selectByExample(example);
            try {
                for (SeckillGoods seckillGoods : seckillGoodsList) {
                    //存入redis数据库中
                    redisTemplate.boundHashOps("seckillGoodsList").put(seckillGoods.getId(),seckillGoods);
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
            //返回秒杀商品列表
            return seckillGoodsList;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 根据秒杀商品id查询商品
     *
     * @param id
     * @return SeckillGoods
     */
    @Override
    public SeckillGoods findOneFromRedis(Long id) {
        try {
            return (SeckillGoods) redisTemplate.boundHashOps("seckillGoodsList").get(id);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
