package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.SeckillGoods;
import com.pinyougou.service.SeckillGoodsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 秒杀商品控制器
 *
 * @author NTP
 * @date 2018/12/16
 */
@RestController
@RequestMapping("/seckill")
public class SeckillGoodsController {
    @Reference(timeout = 10000)
    private SeckillGoodsService seckillGoodsService;

    /**
     * 查询秒杀的商品集合
     *
     * @return List<SeckillGoods>
     */
    @GetMapping("/findSeckillGoods")
    public List<SeckillGoods> findSeckillGoods() {
        return seckillGoodsService.findSeckillGoods();
    }

    /**
     * 根据秒杀商品id查询商品
     * @param id
     * @return SeckillGoods
     */
    @GetMapping("/findOne")
    public SeckillGoods findOne(Long id){
        return seckillGoodsService.findOneFromRedis(id);
    }
}
