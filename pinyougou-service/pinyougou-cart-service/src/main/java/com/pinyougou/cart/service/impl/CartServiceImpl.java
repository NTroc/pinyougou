package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.Cart;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.Item;
import com.pinyougou.pojo.OrderItem;
import com.pinyougou.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author NTP
 * @date 2018/12/10
 */
@Service(interfaceName = "com.pinyougou.service.CartService")
public class CartServiceImpl implements CartService {
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 把Cookie中购物车数据 合并 Redis中购物车
     *
     * @param redisCarts
     * @param cookieCarts
     * @return List<Cart>
     */
    @Override
    public List<Cart> mergeCarts(List<Cart> redisCarts, List<Cart> cookieCarts) {
        // 迭代Cookie中的购物车数据
        for (Cart cart : cookieCarts) {
            // 迭代订单明细
            for (OrderItem orderItem : cart.getOrderItems()) {
                redisCarts = addItemToCart(redisCarts, orderItem.getItemId(), orderItem.getNum());
            }
        }
        return redisCarts;
    }

    /**
     * 修改指定商品数量
     *
     * @param carts
     * @param itemId
     * @param num
     * @return List<Cart>
     */
    @Override
    public List<Cart> updateItemNum(List<Cart> carts, Long itemId, Integer num) {
        try {
            // 1. 根据SKU商品ID查询SKU商品对象
            Item item = itemMapper.selectByPrimaryKey(itemId);
            // 2. 根据商家的id查询商家对应的购物车
            Cart cart = searchCartBySellerId(carts, item.getSellerId());
            // 获取商家的购物车集合
            List<OrderItem> orderItems = cart.getOrderItems();
            // 根据商品的id到商家的购物车集合中查询商品
            OrderItem orderItem = searchOrderItemByItemId(orderItems, itemId);
            orderItem.setNum(num);
            orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * num));
            //判断购买数量
            if (orderItem.getNum() == 0) {
                //从商家购物车集合中删除该商品
                orderItems.remove(orderItem);
            }
            //判断商家购物车集合大小
            if (orderItems.size() == 0) {
                //从用户集合删除该商家的购物车
                carts.remove(cart);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return carts;
    }

    /**
     * 从Redis中查询购物车
     *
     * @param userName 用户名
     * @return List<Cart> 购物车
     */
    @Override
    public List<Cart> findCartByRedis(String userName) {
        try {
            List<Cart> carts = (List<Cart>) redisTemplate.boundValueOps("cart_" + userName).get();
            if (carts == null) {
                carts = new ArrayList<>();
            }
            return carts;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 将购物车保存到Redis
     *
     * @param userName 用户名
     * @param carts    购物车
     */
    @Override
    public void saveCartToRedis(String userName, List<Cart> carts) {
        redisTemplate.boundValueOps("cart_" + userName).set(carts);
    }

    /**
     * 添加SKU商品到购物车
     *
     * @param carts  购物车(一个Cart对应一个商家)
     * @param itemId SKU商品id
     * @param num    购买数据
     * @return 修改后的购物车
     */
    @Override
    public List<Cart> addItemToCart(List<Cart> carts, Long itemId, Integer num) {
        try {
            // 1. 根据SKU商品ID查询SKU商品对象
            Item item = itemMapper.selectByPrimaryKey(itemId);
            // 2. 根据商家的id查询商家对应的购物车
            Cart cart = searchCartBySellerId(carts, item.getSellerId());
            //3.判断用户有没有买过该商家的商品
            if (cart == null) {
                // 创建新的购物车
                cart = new Cart();
                // 设置商家id
                cart.setSellerId(item.getSellerId());
                // 设置商家店铺名称
                cart.setSellerName(item.getSeller());
                // 创建商家对应的购物车集合
                List<OrderItem> orderItems = new ArrayList<>();
                // 创建购买的商品
                OrderItem orderItem = createOrderItem(item, num);
                // 添加购买的商品
                orderItems.add(orderItem);
                // 设置购物车列表
                cart.setOrderItems(orderItems);
                // 把商家的购物车添加到用户购物车集合
                carts.add(cart);
            } else {
                // 4. 用户购买过该商家的商品，有没有买过一样的商品?
                // 获取商家的购物车集合
                List<OrderItem> orderItems = cart.getOrderItems();
                // 根据商品的id到商家的购物车集合中查询商品
                OrderItem orderItem = searchOrderItemByItemId(orderItems, itemId);
                //判断有没有买过一样的商品
                if (orderItem == null) {
                    // 代表没买过该商家的该商品
                    // 创建新的商品
                    orderItem = createOrderItem(item, num);
                    // 添加到商家的购物车
                    orderItems.add(orderItem);
                } else {
                    // 代表购买过该商家的该商品
                    // 购买数量相加
                    orderItem.setNum(orderItem.getNum() + num);
                    // 购买的总金额
                    orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum()));
                    //判断购买数量
                    if (orderItem.getNum() == 0) {
                        //从商家购物车集合中删除该商品
                        orderItems.remove(orderItem);
                    }
                    //判断商家购物车集合大小
                    if (orderItems.size() == 0) {
                        //从用户集合删除该商家的购物车
                        carts.remove(cart);
                    }
                }
            }
            return carts;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    // 根据商品的id到商家的购物车集合中查询商品
    private OrderItem searchOrderItemByItemId(List<OrderItem> orderItems, Long itemId) {
        for (OrderItem orderItem : orderItems) {
            if (orderItem.getItemId().equals(itemId)) {
                return orderItem;
            }
        }
        return null;
    }

    /**
     * 把Item转化成OrderItem
     */
    private OrderItem createOrderItem(Item item, Integer num) {
        // 创建OrderItem
        OrderItem orderItem = new OrderItem();
        // 设置SKU商品的id
        orderItem.setItemId(item.getId());
        // 设置SPU商品的id
        orderItem.setGoodsId(item.getGoodsId());
        // 设置商品的标题
        orderItem.setTitle(item.getTitle());
        // 设置商品的价格
        orderItem.setPrice(item.getPrice());
        // 购买数量
        orderItem.setNum(num);
        // 购买总金额(小计)
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
        // 设置商品图片
        orderItem.setPicPath(item.getImage());
        // 设置商家的id
        orderItem.setSellerId(item.getSellerId());
        return orderItem;
    }


    /**
     * 根据商家的id查询商家对应的购物车
     */
    private Cart searchCartBySellerId(List<Cart> carts, String sellerId) {
        // 迭代购物车集合
        for (Cart cart : carts) {
            // 判断商家id
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }
}
