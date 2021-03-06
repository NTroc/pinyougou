package com.pinyougou.service;

import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Goods;
import com.pinyougou.pojo.Item;
import com.pinyougou.solr.SolrItem;

import java.util.List;
import java.io.Serializable;
import java.util.Map;

/**
 * GoodsService 服务接口
 * @date 2018-11-18 21:04:19
 * @version 1.0
 */
public interface GoodsService {

	/** 添加方法 */
	void save(Goods goods);

	/** 修改方法 */
	void update(Goods goods);

	/** 根据主键id删除 */
	void delete(Serializable id);

	/** 批量删除 */
	void deleteAll(Serializable[] ids);

	/** 根据主键id查询 */
	Goods findOne(Serializable id);

	/** 查询全部 */
	List<Goods> findAll();

	/** 多条件分页查询 */
	PageResult findByPage(Goods goods, int page, int rows);

	/**
	 * 修改状态码
	 * @param ids
	 * @param status
	 */
	void updateStatus(String columnName,Long[] ids, String status);

	/** 根据主键id获取商品信息 */
    Map<String,Object> getGoods(Long goodsId);

	/** 查询上架的SKU商品数据 */
	List<Item> findItemByGoodsId(Long[] ids);

}