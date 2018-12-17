package com.pinyougou.seckill.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.SeckillOrder;
import com.pinyougou.service.SeckillOrderService;
import com.pinyougou.service.WeixinPayService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 秒杀订单控制器
 *
 * @author NTP
 * @date 2018/12/16
 */
@RestController
@RequestMapping("/order")
public class SeckillOrderController {

    @Reference(timeout = 10000)
    private SeckillOrderService seckillOrderService;
    @Reference(timeout = 10000)
    private WeixinPayService weixinPayService;

    /**
     * 提交订单到Redis
     *
     * @param id
     * @param request
     * @return boolean
     */
    @GetMapping("/submitOrder")
    public boolean submitOrder(Long id, HttpServletRequest request) {
        try {
            // 获取登录用户名
            String userId = request.getRemoteUser();
            // 提交订单到Redis
            seckillOrderService.submitOrderToRedis(id, userId);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }


    /**
     * 生成微信支付二维码
     */
    @GetMapping("/genPayCode")
    public Map<String, Object> genPayCode(HttpServletRequest request) {
        // 获取登录用名
        String userId = request.getRemoteUser();
        // 从Redis数据库获取秒订单
        SeckillOrder seckillOrder = seckillOrderService.findOrderFromRedis(userId);
        // 支付金额
        long money = (long) (seckillOrder.getMoney().doubleValue() * 100);
        // 生成支付二维码
        return weixinPayService.genPayCode(seckillOrder.getId().toString(), String.valueOf(money));
    }

    /**
     * 检测支付状态
     * @param outTradeNo
     * @param request
     * @return Map<String, Integer>
     */
    @GetMapping("/queryPayStatus")
    public Map<String, Integer> queryPayStatus(String outTradeNo, HttpServletRequest request) {
        // 定义响应数据
        // {status : 1|2|3} 1:支付成功、2:未支付、3:支付失败
        Map<String, Integer> data = new HashMap<>();
        data.put("status", 3);
        try {

            // 调用支付服务接口
            Map<String, String> resMap = weixinPayService.queryPayStatus(outTradeNo);
            if (resMap != null && resMap.size() > 0) {
                // 判断交易状态码
                if ("SUCCESS".equals(resMap.get("trade_state"))) {
                    // 支付成功
                    // 同步秒杀订单到数据库
                    // 获取登录用户名
                    String userId = request.getRemoteUser();
                    seckillOrderService.saveOrder(userId, resMap.get("transaction_id"));

                    data.put("status", 1);
                }
                if ("NOTPAY".equals(resMap.get("trade_state"))) {
                    // 未支付
                    data.put("status", 2);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }

}
