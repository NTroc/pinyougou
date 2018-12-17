package com.pinyougou.service;

import com.pinyougou.cart.Cart;

import java.util.List;

/**
 * @author NTP
 * @date 2018/12/10
 */
public interface CartService {
    /**
     * 添加SKU商品到购物车
     * @param carts 购物车(一个Cart对应一个商家)
     * @param itemId SKU商品id
     * @param num 购买数据
     * @return 修改后的购物车
     */
    List<Cart> addItemToCart(List<Cart> carts, Long itemId, Integer num);

    /**
     * 从Redis中查询购物车
     * @param userName 用户名
     * @return List<Cart> 购物车
     */
    List<Cart> findCartByRedis(String userName);

    /**
     * 将购物车保存到Redis
     * @param userName 用户名
     * @param carts 购物车
     */
    void saveCartToRedis(String userName, List<Cart> carts);

    /**
     * 修改指定商品数量
     * @param carts
     * @param itemId
     * @param num
     * @return List<Cart>
     */
    List<Cart> updateItemNum(List<Cart> carts, Long itemId, Integer num);

    /**
     * 合并购物车
     * @param redisCarts
     * @param cookieCarts
     * @return List<Cart>
     */
    List<Cart> mergeCarts(List<Cart> redisCarts, List<Cart> cookieCarts);
}
