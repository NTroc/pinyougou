package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.User;
import com.pinyougou.service.SmsService;
import com.pinyougou.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author NTP
 * @date 2018/12/8
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Reference(timeout = 10000)
    private UserService userService;

    /** 注册用户 */
    @PostMapping("/save")
    public boolean save(@RequestBody User user,String smsCode) {
        try {
            //判断验证码是否正确
            if (userService.checkSmsCode(user.getPhone(),smsCode)){
                userService.save(user);
                return true;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /** 发送验证码 */
    @PostMapping("/sendCode")
    public boolean sendCode(String phone){
        try {
            /** 发送验证码 */
            return userService.sendCode(phone);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

}
