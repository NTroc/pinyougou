package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.TypeTemplate;

import java.util.List;
import java.util.Map;

/**
 * 类型模板数据访问接口
 *
 * @version 1.0
 * @date 2018-11-18 21:03:13
 */
public interface TypeTemplateMapper extends Mapper<TypeTemplate> {
    List<TypeTemplate> findAll(TypeTemplate typeTemplate);

    @Select("select id,name from tb_type_template order by id asc")
    List<Map<String, Object>> findAllByIdAndName();
}