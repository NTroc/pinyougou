package com.pinyougou.sms.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.SmsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 短信控制器
 *
 * @author NTP
 * @date 2018/12/8
 */
@RestController
@RequestMapping("/sms")
public class SmsController {

    @Reference(timeout = 10000)
    private SmsService smsService;

    /**
     * 短信发送方法
     *
     * @param phone
     * @param signName
     * @param templateCode
     * @param templateParam
     * @return Map<String   ,       Object> 需要返回json格式的数据
     */
    @PostMapping("/sendSms")
    public Map<String, Object> sendSms(String phone, String signName,
                                       String templateCode, String templateParam) {
        //发送短信
        boolean success = smsService.sendSms(phone, signName, templateCode, templateParam);
        System.out.println(success);
        //返回数据结果
        Map<String, Object> data = new HashMap<>();
        data.put("success", success);
        return data;
    }
}
