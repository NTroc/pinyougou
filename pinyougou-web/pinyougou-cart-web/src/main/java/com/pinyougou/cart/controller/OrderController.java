package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.pojo.Order;
import com.pinyougou.pojo.PayLog;
import com.pinyougou.service.OrderService;
import com.pinyougou.service.WeixinPayService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 订单控制器
 *
 * @author NTP
 * @date 2018/12/15
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference(timeout = 10000)
    private OrderService orderService;
    /**微信支付服务接口*/
    @Reference(timeout=10000)
    private WeixinPayService weixinPayService;

    /**
     * 保存订单
     * @param order
     * @param request
     * @return boolean
     */
    @PostMapping("/save")
    public boolean save(@RequestBody Order order, HttpServletRequest request) {
        try {
            // 获取登录用户名
            String userId = request.getRemoteUser();
            order.setUserId(userId);
            // 设置订单来源 PC端
            order.setSourceType("2");
            orderService.save(order);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }



    /** 生成微信支付二维码 */
    @GetMapping("/genPayCode")
    public Map<String, Object> genPayCode(HttpServletRequest request){
        /** 获取登录用户名 */
        String userId = request.getRemoteUser();
        /** 从Redis查询支付日志 */
        PayLog payLog = orderService.findPayLogFromRedis(userId);
        /** 调用生成微信支付二维码服务方法 */
        return weixinPayService.genPayCode(payLog.getOutTradeNo(),String.valueOf(payLog.getTotalFee()));
    }

    @GetMapping("/queryPayStatus")
    public Map<String,Integer> queryPayStatus(String outTradeNo){
        //定义相应数据
        Map<String,Integer> data = new HashMap<>();
        //{status : 1|2|3} 1:支付成功、2:未支付、3:支付失败
        data.put("status",3);
        try {
            Map<String,String> resMap = weixinPayService.queryPayStatus(outTradeNo);
            if (resMap != null && resMap.size()>0){
                //判断交易状态吗
                if ("SUCCESS".equals(resMap.get("trade_state"))){
                    //支付成功
                    data.put("status",1);
                    /** 修改订单状态 */
                    orderService.updateOrderStatus(outTradeNo, resMap.get("transaction_id"));

                }
                if ("NOTPAY".equals(resMap.get("trade_state"))){
                    //支付成功
                    data.put("status",2);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return data;
    }

}
