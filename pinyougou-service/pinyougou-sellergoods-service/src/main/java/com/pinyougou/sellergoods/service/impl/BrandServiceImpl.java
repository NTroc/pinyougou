package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.mapper.BrandMapper;
import com.pinyougou.pojo.Brand;
import com.pinyougou.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 品牌持久层实现类
 *
 * @author NTP
 * @date 2018/11/17
 */
@Service(interfaceName = "com.pinyougou.service.BrandService")
@Transactional
public class BrandServiceImpl implements BrandService {
    /**
     * 注入数据访问接口代理对象
     */
    @Autowired
    private BrandMapper brandMapper;

    /**
     * 添加方法
     *
     * @param brand
     */
    @Override
    public void save(Brand brand) {
        try {
            //选择性添加（他会判断brand实体中的属性是否有值，有值就生成到insert语句中）
            brandMapper.insertSelective(brand);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 修改方法
     *
     * @param brand
     */
    @Override
    public void update(Brand brand) {
        //选择性修改（他会判断brand实体中的属性是否有值，有值就生成到update语句中）
        brandMapper.updateByPrimaryKeySelective(brand);
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
            // DELETE FROM tb_brand WHERE ( id in ( ? , ? ) )
            //创建示范对象
            Example example = new Example(Brand.class);
            //创建条件对象
            Example.Criteria criteria = example.createCriteria();
            //添加in条件 id in (?,?,?)
            criteria.andIn("id", Arrays.asList(ids));
            //根据条件删除
            brandMapper.deleteByExample(example);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据主键id查询
     *
     * @param id
     */
    @Override
    public Brand findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Brand> findAll() {
        return brandMapper.selectAll();
    }

    /**
     * 多条件分页查询
     *
     * @param brand
     * @param page
     * @param rows
     */
    @Override
    public PageResult findByPage(Brand brand, int page, int rows) {
        //开始分页
        PageInfo<Brand> pageInfo = PageHelper.startPage(page, rows)
                .doSelectPageInfo(new ISelect() {
                    @Override
                    public void doSelect() {
                        brandMapper.findAll(brand);
                    }
                });
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 查询所有的品牌(id与name)
     */
    @Override
    public List<Map<String, Object>> findAllByIdAndName() {
        try {
            return brandMapper.findAllByIdAndName();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
