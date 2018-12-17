package com.pinyougou.user.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 登陆控制器
 * @author NTP
 * @date 2018/12/10
 */
@RestController
public class LoginController {

    /** 获取登陆用户名 */
    @GetMapping("/user/showName")
    public Map<String, String> showName() {
        Map<String, String> data = new HashMap<>();
        String loginName = SecurityContextHolder.getContext().getAuthentication().getName();
        data.put("loginName", loginName);
        return data;
    }
}
