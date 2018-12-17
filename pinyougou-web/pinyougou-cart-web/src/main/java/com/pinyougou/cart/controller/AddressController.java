package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Address;
import com.pinyougou.service.AddressService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 用户地址控制器
 *
 * @author NTP
 * @date 2018/12/14
 */
@RestController
@RequestMapping("/order")
public class AddressController {

    @Reference(timeout = 10000)
    private AddressService addressService;

    /**
     * 获取登录用户的地址列表
     *
     * @param request
     * @return List<Address>
     */
    @GetMapping("/findAddressByUser")
    public List<Address> findAddressByUser(HttpServletRequest request) {
        //获取登陆用户名
        String userId = request.getRemoteUser();
        return addressService.findAddressByUser(userId);
    }

    /**
     * 添加地址
     *
     * @param address
     * @return
     */
    @PostMapping("/saveAddress")
    public boolean save(@RequestBody Address address, HttpServletRequest request) {
        try {
            //获取登陆用户名
            String userId = request.getRemoteUser();
            //设置用户名
            address.setUserId(userId);
            //设置是否默认
            address.setIsDefault("0");
            //设置创建时间
            address.setCreateDate(new Date());
            addressService.save(address);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 修改地址
     *
     * @param address
     * @return
     */
    @PostMapping("/updateAddress")
    public boolean update(@RequestBody Address address) {
        try {
            addressService.update(address);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @GetMapping("/deleteAddress")
    public boolean delete(Long addressId) {
        try {
            addressService.delete(addressId);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
