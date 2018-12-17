package com.pinyougou.service;

import com.pinyougou.pojo.Address;
import java.util.List;
import java.io.Serializable;
/**
 * AddressService 服务接口
 * @date 2018-11-18 21:04:19
 * @version 1.0
 */
public interface AddressService {

    /**
     * 添加地址
     * @param address
     */
	void save(Address address);

    /**
     * 修改地址
     * @param address
     */
	void update(Address address);

	/** 根据主键id删除 */
	void delete(Serializable id);

	/** 批量删除 */
	void deleteAll(Serializable[] ids);

	/** 根据主键id查询 */
	Address findOne(Serializable id);

	/** 查询全部 */
	List<Address> findAll();

	/** 多条件分页查询 */
	List<Address> findByPage(Address address, int page, int rows);

	/**
	 * 获取登录用户的地址列表
	 * @param userId
	 * @return List<Address>
	 */
    List<Address> findAddressByUser(String userId);
}