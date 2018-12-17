package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Goods;
import com.pinyougou.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品管理控制器
 * @author NTP
 * @date 2018/11/26
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {
    /*注入*/
    @Reference(timeout = 10000)
    private GoodsService goodsService;

    /**
     * 多条件分页查询
     * @param goods
     * @param page
     * @param rows
     * @return PageResult
     */
    @GetMapping("/findByPage")
    public PageResult findByPage(Goods goods,Integer page,Integer rows){
        try {
            //添加查询条件
            goods.setAuditStatus("0");
            //get请求转码
            if (goods !=null && StringUtils.isNoneBlank(goods.getGoodsName())){
                goods.setGoodsName(new String(goods.getGoodsName().getBytes("ISO8859-1"),"UTF-8"));
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return goodsService.findByPage(goods,page,rows);
    }

    /**
     * 修改审核状态码
     * @param ids
     * @param status
     * @return boolean
     */
    @GetMapping("/updateStatus")
    public boolean updateStatus(Long[] ids,String status){
        try {
            goodsService.updateStatus("audit_status",ids,status);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 修改删除状态码
     * @param ids
     * @return boolean
     */
    @GetMapping("/delete")
    public boolean delete(Long[] ids){
        try{

            goodsService.updateStatus("is_delete",ids,"1");
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
}
