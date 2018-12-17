package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Goods;

import java.util.List;
import java.util.Map;

/**
 * GoodsMapper 数据访问接口
 *
 * @version 1.0
 * @date 2018-11-18 21:03:13
 */
public interface GoodsMapper extends Mapper<Goods> {
    /**
     * 分页条件查询
     */
    List<Map<String, Object>> findAll(Goods goods);

    /**
     * 修改状态码
     *
     * @param ids
     * @param status
     */
    void updateStatus(@Param("columnName")String columnName,@Param("ids") Long[] ids, @Param("status") String status);
}