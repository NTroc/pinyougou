package com.pinyougou.service;

import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.TypeTemplate;
import java.util.List;
import java.io.Serializable;
import java.util.Map;

/**
 * TypeTemplateService 服务接口
 * @date 2018-11-18 21:04:19
 * @version 1.0
 */
public interface TypeTemplateService {

	/** 添加方法 */
	void save(TypeTemplate typeTemplate);

	/** 修改方法 */
	void update(TypeTemplate typeTemplate);

	/** 根据主键id删除 */
	void delete(Serializable id);

	/** 批量删除 */
	void deleteAll(Serializable[] ids);

	/** 根据主键id查询 */
	TypeTemplate findOne(Serializable id);

	/** 查询全部 */
	List<TypeTemplate> findAll();

	/** 多条件分页查询 */
	PageResult findByPage(TypeTemplate typeTemplate, int page, int rows);

	/** 查询所有的类型模板(id与name) */
	List<Map<String,Object>> findAllByIdAndName();


	/** 查询该模版对应的规格与规格选项 */
    List<Map> findSpecByTemplateId(Long id);
}