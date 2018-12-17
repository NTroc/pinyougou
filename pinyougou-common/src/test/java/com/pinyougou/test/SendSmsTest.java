package com.pinyougou.test;

import com.pinyougou.common.util.HttpClientUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NTP
 * @date 2018/12/8
 */
public class SendSmsTest {
    public static void main(String[] args) {
        //创建httpclientUtil
        HttpClientUtils httpClientUtils = new HttpClientUtils(false);
        /**
         *  phone           String  必须  待发送手机号
            signName        String  必须  短信签名-可在短信控制台中找到
            templateCode    String 必须  短信模板-可在短信控制台中找到
            templateParam   String 必须 模板中的变量替换 JSON 串,
                            如模板内容为"亲爱的${name},您的验证码为${code}"时,
                            此处的值为{"name" : "", "code" : ""}
         */
        //封装请求参数
        Map<String,String> params = new HashMap<>();
        /*params.put("phone","15173073139");
        params.put("signName","五子连珠");
        params.put("templateCode","SMS_11480310");
        params.put("templateParam","{'number':'123456'}");*/

        params.put("phone","15173073139");
        params.put("signName","吴建辉");
        params.put("templateCode","SMS_118765104");
        params.put("templateParam","{'code':'"+8848+"'}");
        //发送post请求
        String sendPost = httpClientUtils.sendPost("http://sms.pinyougou.com/sms/sendSms", params);
        System.out.println(sendPost);
    }
}
