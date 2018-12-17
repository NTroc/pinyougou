package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Seller;
import com.pinyougou.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * 商家控制器
 *
 * @author NTP
 * @date 2018/11/23
 */
@RestController
@RequestMapping("/seller")
public class SellerController {
    /**
     * 注入商家服务接口代理对象
     */
    @Reference(timeout = 10000)
    public SellerService sellerService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 添加商家
     * @param seller
     * @return Boolean
     */
    @PostMapping("/save")
    public Boolean save(@RequestBody Seller seller) {
        try {
            /** 密码加密 */
            String password = passwordEncoder.encode(seller.getPassword());
            seller.setPassword(password);
            sellerService.save(seller);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }


    /**
     * 修改商家信息
     * @param seller
     * @return Boolean
     */
    @PostMapping("/update")
    public Boolean update(@RequestBody Seller seller){
        try {
            sellerService.update(seller);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /*根据登陆用户名查询商家信息*/
    @GetMapping("/findSellerBySellerId")
    public Seller findSellerBySellerId(){
        try {
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            return sellerService.findOne(sellerId);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    /** 修改用户密码 */
    @GetMapping("/updatePassword")
    public Boolean updatePassword(String oldPassword,String newPassword){
        //获取用户名
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        //获取数据库中用户名原密码
        String password = sellerService.findPasswordBySellerId(sellerId);
        //判断输入的密码与原始密码是否相同
        if (passwordEncoder.matches(oldPassword,password)){
            //相同，修改密码
            Seller seller = new Seller();
            seller.setSellerId(sellerId);
            String encodePassword = passwordEncoder.encode(newPassword);
            seller.setPassword(encodePassword);
            sellerService.update(seller);
            return true;
        }else {
            return false;
        }
    }
}
