package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Goods;
import com.pinyougou.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * @author NTP
 * @date 2018/11/24
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Reference(timeout = 10000)
    private GoodsService goodsService;

    /**
     * 保存商品
     * @param goods
     * @return
     */
    @PostMapping("/save")
    public Boolean save(@RequestBody Goods goods) {
        try {
            /** 获取登录用户名 */
            String loginName = SecurityContextHolder.getContext().getAuthentication().getName();
            /** 设置商家ID */
            goods.setSellerId(loginName);
            System.out.println(goods);
            goodsService.save(goods);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 多条件分页查询
     * @param goods
     * @param page
     * @param rows
     * @return PageResult
     */
    @GetMapping("/findByPage")
    public PageResult findByPage(Goods goods, Integer page, Integer rows) {
        try {
            /** 获取登录商家编号 */
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            /** 添加查询条件 */
            goods.setSellerId(sellerId);
            //ge请求转码
            if (goods != null && StringUtils.isNoneBlank(goods.getGoodsName())){
                goods.setGoodsName(new String(goods.getGoodsName().getBytes("ISO8859-1"),"UTF-8"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goodsService.findByPage(goods,page,rows);
    }


    @GetMapping("/delete")
    public boolean delete(Long[] ids){
        try {
            goodsService.updateStatus("is_delete",ids,"1");
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

}
