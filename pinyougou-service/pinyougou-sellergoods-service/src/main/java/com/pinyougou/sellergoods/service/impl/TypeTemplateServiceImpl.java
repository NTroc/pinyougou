package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.mapper.TypeTemplateMapper;
import com.pinyougou.pojo.SpecificationOption;
import com.pinyougou.pojo.TypeTemplate;
import com.pinyougou.service.TypeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * TypeTemplateServiceImpl 服务接口实现类
 *
 * @version 1.0
 * @date 2018-11-18 21:04:19
 */
@Service(interfaceName = "com.pinyougou.service.TypeTemplateService")
@Transactional
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TypeTemplateMapper typeTemplateMapper;
    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    /**
     * 添加方法
     *
     * @param typeTemplate
     */
    @Override
    public void save(TypeTemplate typeTemplate) {
        typeTemplateMapper.insertSelective(typeTemplate);
    }

    /**
     * 修改方法
     *
     * @param typeTemplate
     */
    @Override
    public void update(TypeTemplate typeTemplate) {
        typeTemplateMapper.updateByPrimaryKeySelective(typeTemplate);
    }

    /**
     * 根据主键id删除
     *
     * @param id
     */
    @Override
    public void delete(Serializable id) {

    }

    /**
     * 批量删除
     *
     * @param ids
     */
    @Override
    public void deleteAll(Serializable[] ids) {
        try {
            /*创建示范对象*/
            Example example = new Example(TypeTemplate.class);
            //创建条件对象
            Example.Criteria criteria = example.createCriteria();
            /*添加in条件*/
            criteria.andIn("id", Arrays.asList(ids));
            /*条件删除*/
            typeTemplateMapper.deleteByExample(example);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 根据主键id查询
     *
     * @param id
     */
    @Override
    public TypeTemplate findOne(Serializable id) {
        try {
            return typeTemplateMapper.selectByPrimaryKey(id);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 查询全部
     */
    @Override
    public List<TypeTemplate> findAll() {
        try {
            return typeTemplateMapper.selectAll();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 多条件分页查询
     */
    public PageResult findByPage(TypeTemplate typeTemplate, int page, int rows) {
        try {
            PageInfo<TypeTemplate> pageInfo = PageHelper.startPage(page, rows)
                    .doSelectPageInfo(new ISelect() {
                        @Override
                        public void doSelect() {
                            typeTemplateMapper.findAll(typeTemplate);
                        }
                    });
            return new PageResult(pageInfo.getTotal(), pageInfo.getList());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 查询所有的类型模板(id与name)
     */
    @Override
    public List<Map<String, Object>> findAllByIdAndName() {
        try {
            return typeTemplateMapper.findAllByIdAndName();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /** 查询该模版对应的规格与规格选项 */
    public List<Map> findSpecByTemplateId(Long id){

        /**
         * [{"id":33,"text":"电视屏幕尺寸"}]
         * 获取模版中所有的规格，转化成  List<Map>
         */
        //1.获取模版中spec_ids的数据
        TypeTemplate typeTemplate = findOne(id);
        //json字符串数组
        String specIds = typeTemplate.getSpecIds();
        //把json字符串数组转换成List<Map>
        //JSON.parseArray() :  [{},{}]
        //JSON.parseObject() : {}
        List<Map> specList = JSON.parseArray(typeTemplate.getSpecIds(),Map.class);
        /** 迭代模版中所有的规格 */
        for (Map map : specList) {
            //map ：{"id":33,"text":"电视屏幕尺寸"}
            /** 创建查询条件对象 */
            SpecificationOption specificationOption = new SpecificationOption();
            specificationOption.setSpecId(Long.valueOf(map.get("id").toString()));
            List<SpecificationOption> specificationOptions = specificationOptionMapper.select(specificationOption);

            //map : {"id":33,"text":"电视屏幕尺寸",options:[{},{}]}
            map.put("options",specificationOptions);
        }
        return specList;
    }
}