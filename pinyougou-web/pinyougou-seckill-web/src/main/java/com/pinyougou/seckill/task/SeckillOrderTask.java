package com.pinyougou.seckill.task;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.SeckillOrder;
import com.pinyougou.service.SeckillOrderService;
import com.pinyougou.service.WeixinPayService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 秒杀订单任务
 *
 * @author NTP
 * @date 2018/12/17
 */
@Component
public class SeckillOrderTask {
    @Reference(timeout = 10000)
    private SeckillOrderService seckillOrderService;
    @Reference(timeout = 10000)
    private WeixinPayService weixinPayService;

    /**
     * 定时任务的方法(间隔3秒，关闭秒杀超时未支付定单)
     * cron: 触发任务调度的时间表达式 6个字符串
     * 秒 分 小时  日 月  周
     */
    @Scheduled(cron = "0/3 * * * * *")
    public void closeOrderTask() {
        System.out.println("当前时间：" + new Date());
        // 1. 查询超时5分钟还未支付的订单
        List<SeckillOrder> seckillOrderList = seckillOrderService.findOrderByTimeout();

        System.out.println("超时5分钟的未支付的订单的总数：" + seckillOrderList.size());

        // 循环所有超时5分钟的未支付的订单
        for (SeckillOrder seckillOrder : seckillOrderList) {

            // 2. 调用微信支付服务接口，关闭微信订单
            Map<String, String> resMap = weixinPayService.closePayTimeout(seckillOrder.getId().toString());
            // 判断关单状态
            if (resMap != null && resMap.size() > 0) {
                if ("SUCCESS".equals(resMap.get("return_code"))) { // 关单成功
                    // 3. 删除超时未支付订单
                    seckillOrderService.deleteOrderFromRedis(seckillOrder);
                }
            }
        }

    }
}
