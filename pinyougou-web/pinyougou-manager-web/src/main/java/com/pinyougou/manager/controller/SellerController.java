package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Seller;
import com.pinyougou.service.SellerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author NTP
 * @date 2018/11/23
 */
@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference(timeout = 10000)
    private SellerService sellerService;

    @GetMapping("/findByPage")
    public PageResult findByPage(Seller seller, Integer page, Integer rows) {
        try {
            /** GET请求中文转码 */
            if (seller != null && StringUtils.isNoneBlank(seller.getName())) {
                seller.setName(new String(seller.getName().getBytes("ISO8859-1"), "UTF-8"));
            }
            if (seller != null && StringUtils.isNoneBlank(seller.getNickName())) {
                seller.setName(new String(seller.getNickName().getBytes("ISO8859-1"), "UTF-8"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return sellerService.findByPage(seller, page, rows);
    }

    @GetMapping("/updateStatus")
    public Boolean updateStatus(String sellerId,String status){
        try {
            sellerService.updateStatus(sellerId,status);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
}
