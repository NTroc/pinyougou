package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.Cart;
import com.pinyougou.common.util.CookieUtils;
import com.pinyougou.service.CartService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 购物车控制器
 *
 * @author NTP
 * @date 2018/12/10
 */
@RestController
@RequestMapping("/cart")
public class CartController {
    //注入request和response
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @Reference(timeout = 10000)
    private CartService cartService;

    /**
     * 把商品添加到购物车
     *
     * @param itemId
     * @param num
     * @return boolean
     */
    @GetMapping("/addCart")
    @CrossOrigin(origins="http://item.pinyougou.com",allowCredentials="true")
    public boolean addCart(Long itemId, Integer num) {
        try {
            /** 设置允许访问的域名
            response.setHeader("Access-Control-Allow-Origin","http://item.pinyougou.com");
            *//** 设置允许操作Cookie *//*
            response.setHeader("Access-Control-Allow-Credentials","true");*/

            // 获取登录用户名
            String username = request.getRemoteUser();
            // 1. 获取购物车
            List<Cart> carts = findCart();
            // 2. 把商品添加到购物车，返回修改后的购物车
            carts = cartService.addItemToCart(carts, itemId, num);
            if (StringUtils.isNoneBlank(username)) {
                //把购物车保存到Redis
                cartService.saveCartToRedis(username, carts);
            } else {
                // 3. 把添加后的购物车存储到Cookie中
                CookieUtils.setCookie(request, response, CookieUtils.CookieName.PINYOUGOU_CART, JSON.toJSONString(carts),
                        86400, true);
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 修改指定商品数量
     */
    @GetMapping("/updateNum")
    public boolean updateNum(Long itemId, Integer num) {
        try {
            // 获取登录用户名
            String username = request.getRemoteUser();
            // 1. 获取购物车
            List<Cart> carts = findCart();
            // 2. 把商品添加到购物车，返回修改后的购物车
            carts = cartService.updateItemNum(carts, itemId, num);
            if (StringUtils.isNoneBlank(username)) {
                //已登录
                //把购物车保存到Redis
                cartService.saveCartToRedis(username, carts);
                //获取Cookie中的购物车列表
                String cartStr = CookieUtils.getCookieValue(request, CookieUtils.CookieName.PINYOUGOU_CART, true);
                //判断是否为空
                if (StringUtils.isBlank(cartStr)) {
                    cartStr = "[]";
                }
                // 把json格式字符串转化成List
                carts = JSON.parseArray(cartStr, Cart.class);
            } else {
                //未登录
                // 3. 把添加后的购物车存储到Cookie中
                CookieUtils.setCookie(request, response, CookieUtils.CookieName.PINYOUGOU_CART, JSON.toJSONString(carts),
                        86400, true);
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /***
     * 获取购物车集合
     * @return List<Cart>
     */
    @GetMapping("/findCart")
    public List<Cart> findCart() {
        //获取登陆用户名
        String userName = request.getRemoteUser();
        // 定义购物车集合
        List<Cart> carts = null;
        // 判断用户是否登录
        if (StringUtils.isNoneBlank(userName)) {
            /**######## 从Redis获取购物车 #######*/
            carts = cartService.findCartByRedis(userName);
            //获取Cookie中的购物车列表
            String cartStr = CookieUtils.getCookieValue(request, CookieUtils.CookieName.PINYOUGOU_CART, true);
            //判断是否为空
            if (StringUtils.isNoneBlank(cartStr)) {
                // 把json格式字符串转化成List
                List<Cart> cookieCarts = JSON.parseArray(cartStr, Cart.class);
                // 合并购物车
                carts = cartService.mergeCarts(carts,cookieCarts);
                // 将合并后的购物车存入Redis
                cartService.saveCartToRedis(userName,carts);
                // 删除Cookie购物车
                CookieUtils.deleteCookie(request,response,CookieUtils.CookieName.PINYOUGOU_CART);
            }
        } else {
            /** ############# 未登录的用户，从Cookie中获取购物车数据 #################*/
            // 获取原来的购物车(Cookie) List<Cart>的json格式字符口串 [{},{},{}]
            //获取Cookie中的购物车列表
            String cartStr = CookieUtils.getCookieValue(request, CookieUtils.CookieName.PINYOUGOU_CART, true);
            //判断是否为空
            if (StringUtils.isBlank(cartStr)) {
                cartStr = "[]";
            }
            // 把json格式字符串转化成List
            carts = JSON.parseArray(cartStr, Cart.class);
        }
        return carts;
    }
}
