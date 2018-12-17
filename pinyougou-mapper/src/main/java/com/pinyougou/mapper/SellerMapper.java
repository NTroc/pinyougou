package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Seller;

import java.util.List;

/**
 * SellerMapper 数据访问接口
 *
 * @version 1.0
 * @date 2018-11-18 21:03:13
 */
public interface SellerMapper extends Mapper<Seller> {


    /** 多条件分页查询 */
    List<Seller> findAll(Seller seller);

    /** 根据用户名查找用户密码 */
    @Select("select password from tb_seller where seller_id = #{sellerId}")
    String findPasswordBySellerId(String sellerId);
}