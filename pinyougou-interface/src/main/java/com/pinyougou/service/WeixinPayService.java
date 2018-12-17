package com.pinyougou.service;

import java.util.Map;

/**
 * 微信支付服务接口
 *
 * @author NTP
 * @date 2018/12/15
 */
public interface WeixinPayService {
    /**
     * 调用微信支付系统的“统一下单”接口
     * 获取支付URL:code_url;
     *
     * @param outTradeNo 订单交易号
     * @param totalFee   金额(分)
     * @return Map集合
     */
    Map<String, Object> genPayCode(String outTradeNo, String totalFee);

    /**
     * 调用微信支付系统的“查询订单”接口
     * 获取支付状态：trade_state;
     *
     * @param outTradeNo
     * @return Map<String   ,   String>
     */
    Map<String, String> queryPayStatus(String outTradeNo);

    /**
     * 调用微信支付系统的"关闭订单"接口
     * 获取关闭状态: return_code
     *
     * @param outTradeNo
     * @return
     */
    Map<String, String> closePayTimeout(String outTradeNo);
}

