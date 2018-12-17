package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.ItemCat;
import com.pinyougou.service.ItemCatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类控制器
 * @author NTP
 * @date 2018/11/22
 */
@RestController
@RequestMapping("/itemCat")
public class ItemCatcontroller {

    @Reference(timeout = 10000)
    public ItemCatService itemCatService;

    /**
     * 根据父级id查询商品分类
     *
     * @param parentId
     * @return List<ItemCat>
     */
    @GetMapping("/findItemCatByParentId")
    public List<ItemCat> findItemCatByParentId(Long parentId) {
        return itemCatService.findItemCatByParentId(parentId);
    }

    /** 增加商品分类 */
    @PostMapping("/save")
    public Boolean save(@RequestBody ItemCat itemCat){
        try {
            itemCatService.save(itemCat);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /** 修改商品分类 */
    @PostMapping("/update")
    public Boolean update(@RequestBody ItemCat itemCat){
        try {
            itemCatService.update(itemCat);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /** 删除商品分类 */
    @GetMapping("/delete")
    public Boolean delete(Long[] ids){
        try {
            itemCatService.deleteAll(ids);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
}
