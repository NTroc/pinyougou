package com.pinyougou.cart.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 登陆控制器
 *
 * @author NTP
 * @date 2018/12/10
 */
@RestController
public class LoginController {

    /** 显示用户名 */
    @GetMapping("/user/showName")
    public Map<String, String> showName(HttpServletRequest request) {
        Map<String, String> data = new HashMap<>();
        /** 获取登录用户名 */
        String loginName = request.getRemoteUser();
        data.put("loginName", loginName);
        return data;
    }
}

