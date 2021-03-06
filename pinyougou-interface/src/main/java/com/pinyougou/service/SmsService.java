package com.pinyougou.service;

/**
 * 短信服务接口
 * @author NTP
 * @date 2018/12/7
 */
public interface SmsService {
    /**
     * 发送短信方法
     *
     * @param phone         手机号码
     * @param signName      签名
     * @param templateCode  短信模板
     * @param templateParam 模板参数(json格式)
     * @return true 发送成功 false 发送失败
     */
    boolean sendSms(String phone, String signName, String templateCode, String templateParam);
}
