package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.mapper.SpecificationMapper;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.pojo.Specification;
import com.pinyougou.pojo.SpecificationOption;
import com.pinyougou.service.SpecificationOptionService;
import com.pinyougou.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * SpecificationServiceImpl 服务接口实现类
 * @date 2018-11-18 21:04:19
 * @version 1.0
 */
@Service(interfaceName = "com.pinyougou.service.SpecificationService")
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

	/** 注入数据访问接口代理对象 */
	@Autowired
	private SpecificationMapper specificationMapper;
	@Autowired
	private SpecificationOptionMapper specificationOptionMapper;

	/** 添加方法 */
	public void save(Specification specification){
		try {
			//选择性添加（他会判断brand实体中的属性是否有值，有值就生成到insert语句中）
			specificationMapper.insertSelective(specification);
			specificationOptionMapper.save(specification);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 修改方法 */
	public void update(Specification specification){
		try {
			// 修改往规格表数据
			specificationMapper.updateByPrimaryKeySelective(specification);
			//修改规格选项表数据
			// 第一步：删除规格选项表中的数据 spec_id
			// delete from tb_specification_option where spec_id = ?
			// 创建规格选项对象，封装删除条件 通用Mapper
			SpecificationOption specificationOption = new SpecificationOption();
			specificationOption.setSpecId(specification.getId());
			specificationOptionMapper.delete(specificationOption);
			// 第二步：往规格选项表插入数据
			specificationOptionMapper.save(specification);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 根据主键id删除 */
	public void delete(Serializable id){
		try {
			specificationMapper.deleteByPrimaryKey(id);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 批量删除规格与规格选项 */
	public void deleteAll(Serializable[] ids){
		try {
			for (Serializable id : ids) {
				SpecificationOption specificationOption = new SpecificationOption();
				specificationOption.setSpecId((Long) id);
				specificationOptionMapper.delete(specificationOption);
				specificationMapper.deleteByPrimaryKey(id);
			}
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 根据主键id查询 */
	public Specification findOne(Serializable id){
		try {
			return specificationMapper.selectByPrimaryKey(id);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 查询全部 */
	public List<Specification> findAll(){
		try {
			return specificationMapper.selectAll();
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 多条件分页查询 */
	public PageResult findByPage(Specification specification, int page, int rows){
		try {
			//开始分页
			PageInfo<Specification> pageInfo = PageHelper.startPage(page, rows)
				.doSelectPageInfo(new ISelect() {
					@Override
					public void doSelect() {
						specificationMapper.findAll(specification);
					}
				});
			return new PageResult(pageInfo.getTotal(),pageInfo.getList());
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}


	/** 查询所有的规格(id与specName) */
	public List<Map<String, Object>> findAllByIdAndName() {
		try {
			return specificationMapper.findAllByIdAndName();
		}catch (Exception e){
			throw new RuntimeException(e);
		}
	}
}