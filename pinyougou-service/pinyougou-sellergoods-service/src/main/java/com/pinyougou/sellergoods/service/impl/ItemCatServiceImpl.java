package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.ItemCatMapper;
import com.pinyougou.pojo.ItemCat;
import com.pinyougou.service.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * 商品分类服务接口实现类
 * @author NTP
 * @date 2018/11/22
 */
@Service(interfaceName = "com.pinyougou.service.ItemCatService")
@Transactional
public class ItemCatServiceImpl implements ItemCatService {
    /**
     * 注入接口
     */
    @Autowired
    private ItemCatMapper itemCatMapper;

    /**
     * 添加方法
     *
     * @param itemCat
     */
    @Override
    public void save(ItemCat itemCat) {
        try {
            itemCatMapper.insertSelective(itemCat);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * 修改方法
     *
     * @param itemCat
     */
    @Override
    public void update(ItemCat itemCat) {
        try {
            itemCatMapper.updateByPrimaryKeySelective(itemCat);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * 根据主键id删除
     *
     * @param id
     */
    @Override
    public void delete(Serializable id) {

    }

    /**
     * 批量删除
     *
     * @param ids
     */
    @Override
    public void deleteAll(Serializable[] ids) {
        try {

        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * 根据主键id查询
     *
     * @param id
     */
    @Override
    public ItemCat findOne(Serializable id) {
        return null;
    }

    /**
     * 查询全部
     */
    @Override
    public List<ItemCat> findAll() {
        return null;
    }

    /**
     * 多条件分页查询
     *
     * @param itemCat
     * @param page
     * @param rows
     */
    @Override
    public List<ItemCat> findByPage(ItemCat itemCat, int page, int rows) {
        return null;
    }

    /**
     * 根据父级id查询商品分类
     *
     * @param parentId
     * @return List<ItemCat>
     */
    @Override
    public List<ItemCat> findItemCatByParentId(Long parentId) {
        try {
            /** 创建ItemCat封装查询条件 */
            ItemCat itemCat = new ItemCat();
            itemCat.setParentId(parentId);
            return itemCatMapper.select(itemCat);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
