package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.AddressMapper;
import com.pinyougou.pojo.Address;
import com.pinyougou.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.List;

/**
 * 用户地址服务接口实现类
 *
 * @author NTP
 * @date 2018/12/14
 */
@Service(interfaceName = "com.pinyougou.service.AddressService")
@Transactional
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    /**
     * 添加方法
     *
     * @param address
     */
    @Override
    public void save(Address address) {
        try {
            addressMapper.insertSelective(address);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 修改方法
     *
     * @param address
     */
    @Override
    public void update(Address address) {
        try {
            addressMapper.updateByPrimaryKeySelective(address);
        } catch (Exception ex) {
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
        try {
            addressMapper.deleteByPrimaryKey(id);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 批量删除
     *
     * @param ids
     */
    @Override
    public void deleteAll(Serializable[] ids) {

    }

    /**
     * 根据主键id查询
     *
     * @param id
     */
    @Override
    public Address findOne(Serializable id) {
        return null;
    }

    /**
     * 查询全部
     */
    @Override
    public List<Address> findAll() {
        return null;
    }

    /**
     * 多条件分页查询
     *
     * @param address
     * @param page
     * @param rows
     */
    @Override
    public List<Address> findByPage(Address address, int page, int rows) {
        return null;
    }

    /**
     * 获取登录用户的地址列表
     *
     * @param userId
     * @return List<Address>
     */
    @Override
    public List<Address> findAddressByUser(String userId) {
        try {
            //SELECT * FROM tb_address WHERE user_id = 'itcast' order is_default desc
            //创建示范对象
            Example example = new Example(Address.class);
            //创建条件对象
            Example.Criteria criteria = example.createCriteria();
            //封装条件对象
            criteria.andEqualTo("userId", userId);
            //加入排序
            example.orderBy("isDefault").desc();
            // 条件查询
            return addressMapper.selectByExample(example);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
