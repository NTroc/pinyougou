package com.pinyougou.mapper;

import com.pinyougou.pojo.Brand;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author NTP
 * @date 2018/11/17
 */
public interface BrandMapper extends Mapper<Brand> {

    List<Brand> findAll(Brand brand);

    @Select("select id,name text from tb_brand order by id asc ")
    List<Map<String, Object>> findAllByIdAndName();
}
