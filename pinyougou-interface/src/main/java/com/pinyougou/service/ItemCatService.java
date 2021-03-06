package com.pinyougou.service;

import com.pinyougou.pojo.ItemCat;

import java.util.List;
import java.io.Serializable;

/**
 * 商品分类服务接口
 *
 * @version 1.0
 * @date 2018-11-18 21:04:19
 */
public interface ItemCatService {

    /**
     * 添加方法
     */
    void save(ItemCat itemCat);

    /**
     * 修改方法
     */
    void update(ItemCat itemCat);

    /**
     * 根据主键id删除
     */
    void delete(Serializable id);

    /**
     * 批量删除
     */
    void deleteAll(Serializable[] ids);

    /**
     * 根据主键id查询
     */
    ItemCat findOne(Serializable id);

    /**
     * 查询全部
     */
    List<ItemCat> findAll();

    /**
     * 多条件分页查询
     */
    List<ItemCat> findByPage(ItemCat itemCat, int page, int rows);

    /**
     * 根据父级id查询商品分类
     * @param parentId
     * @return List<ItemCat>
     */
    List<ItemCat> findItemCatByParentId(Long parentId);
}