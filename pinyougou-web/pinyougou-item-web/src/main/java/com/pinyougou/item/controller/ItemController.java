package com.pinyougou.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.GoodsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * @author NTP
 * @date 2018/12/5
 */
@Controller
public class ItemController {
    @Reference(timeout = 10000)
    private GoodsService goodsService;

    /**
     * 根据主键id获取商品信息
     * http://item.pinyougou.com/5089253.html
     * 5089253  -->  SPU商品表的主键id
     */
    @GetMapping("/{goodsId}")
    public String getGoods(@PathVariable("goodsId") Long goodsId, Model model) {
        // jsp : model中数据最后放到request
        // ftl : model中数据就是FreeMarker的数据模型
        Map<String, Object> data = goodsService.getGoods(goodsId);
        // 把数据添加到数据模型
        model.addAllAttributes(data);
        return "item";
    }
}
