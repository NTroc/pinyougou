package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.common.util.HttpClientUtils;
import com.pinyougou.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付服务接口实现类
 *
 * @author NTP
 * @date 2018/12/15
 */
@Service(interfaceName = "com.pinyougou.service.WeixinPayService")
public class WeixinPayServiceImpl implements WeixinPayService {
    /**
     * 微信公众账号或开放平台APP的唯一标识
     */
    @Value("${appid}")
    private String appid;
    /**
     * 商户账号
     */
    @Value("${partner}")
    private String partner;
    /**
     * 商户密钥
     */
    @Value("${partnerkey}")
    private String partnerkey;

    /**
     * 统一下单接口URL
     */
    @Value("${unifiedorder}")
    private String unifiedorder;
    /**
     * 查询订单接口URL
     */
    @Value("${orderquery}")
    private String orderquery;
    @Value("${closeorder}")
    private String closeorder;

    /**
     * 调用微信支付系统的"统一下单"接口
     * 获取支付URL: code_url
     */
    public Map<String, Object> genPayCode(String outTradeNo, String totalFee) {

        // 定义Map集合封装返回数据
        Map<String, Object> data = new HashMap<>();
        try {
            // 1. 封装请求参数
            Map<String, String> params = new HashMap<>();
            // 公众账号ID	appid
            params.put("appid", appid);
            // 商户号	mch_id
            params.put("mch_id", partner);
            // 随机字符串	nonce_str
            params.put("nonce_str", WXPayUtil.generateNonceStr());
            // 商品描述	body
            params.put("body", "品优购");
            // 商户订单号	out_trade_no
            params.put("out_trade_no", outTradeNo);
            // 订单总金额(分)	total_fee
            params.put("total_fee", totalFee);
            // 终端IP	spbill_create_ip
            params.put("spbill_create_ip", "127.0.0.1");
            // 通知地址	notify_url
            params.put("notify_url", "http://www.pinyougou.com");
            // 交易类型(NATIVE -Native支付)	trade_type
            params.put("trade_type", "NATIVE");

            // 签名	sign (为了调用接口安全、全部的请求参数加签名)
            String xmlParam = WXPayUtil.generateSignedXml(params, partnerkey);
            System.out.println("请求参数: " + xmlParam);


            // 2. 调用"统一下单"接口，获取响应数据
            HttpClientUtils httpClientUtils = new HttpClientUtils(true);
            String xmlData = httpClientUtils.sendPost(unifiedorder, xmlParam);
            System.out.println("响应数据：" + xmlData);
            // 把xml格式的字符串转化成map集合
            Map<String, String> mapData = WXPayUtil.xmlToMap(xmlData);

            // 3. 返回数据
            data.put("outTradeNo", outTradeNo);
            data.put("totalFee", totalFee);
            // 二维码链接	code_url
            data.put("codeUrl", mapData.get("code_url"));

            return data;

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    /**
     * 调用微信支付系统的“查询订单”接口
     * 获取支付状态：trade_state;
     *
     * @param outTradeNo
     * @return Map<String       ,       String>
     */
    @Override
    public Map<String, String> queryPayStatus(String outTradeNo) {
        try {
            // 1. 封装请求参数
            Map<String, String> params = new HashMap<>();
            // 公众账号ID	appid
            params.put("appid", appid);
            // 商户号	mch_id
            params.put("mch_id", partner);
            // 商户订单号	out_trade_no
            params.put("out_trade_no", outTradeNo);
            // 随机字符串	nonce_str
            params.put("nonce_str", WXPayUtil.generateNonceStr());

            // 签名	sign (为了调用接口安全、全部的请求参数加签名)
            String xmlParam = WXPayUtil.generateSignedXml(params, partnerkey);
            System.out.println("请求参数: " + xmlParam);


            // 2. 调用"查询订单"接口，获取响应数据
            HttpClientUtils httpClientUtils = new HttpClientUtils(true);
            String xmlData = httpClientUtils.sendPost(orderquery, xmlParam);
            System.out.println("响应数据：" + xmlData);

            // 3. 把xml格式的字符串转化成map集合，返回数据
            return WXPayUtil.xmlToMap(xmlData);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    /**
     * 调用微信支付系统的"关闭订单"接口
     * 获取关闭状态: return_code
     *
     * @param outTradeNo
     * @return Map<String   ,       String>
     */
    @Override
    public Map<String, String> closePayTimeout(String outTradeNo) {
        try {
            // 1. 封装请求参数
            Map<String, String> params = new HashMap<>();
            // 公众账号ID	appid
            params.put("appid", appid);
            // 商户号	mch_id
            params.put("mch_id", partner);
            // 商户订单号	out_trade_no
            params.put("out_trade_no", outTradeNo);
            // 随机字符串	nonce_str
            params.put("nonce_str", WXPayUtil.generateNonceStr());

            // 签名	sign (为了调用接口安全、全部的请求参数加签名)
            String xmlParam = WXPayUtil.generateSignedXml(params, partnerkey);
            System.out.println("请求参数: " + xmlParam);


            // 2. 调用"关闭订单"接口，获取响应数据
            HttpClientUtils httpClientUtils = new HttpClientUtils(true);
            String xmlData = httpClientUtils.sendPost(closeorder, xmlParam);
            System.out.println("响应数据：" + xmlData);

            // 3. 把xml格式的字符串转化成map集合，返回数据
            return WXPayUtil.xmlToMap(xmlData);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
