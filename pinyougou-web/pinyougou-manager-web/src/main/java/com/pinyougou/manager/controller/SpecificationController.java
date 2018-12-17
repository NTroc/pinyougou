package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Specification;
import com.pinyougou.service.SpecificationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author NTP
 * @date 2018/11/21
 */
@RestController
@RequestMapping("/specification")
public class SpecificationController {

    @Reference(timeout = 10000)
    private SpecificationService specificationService;

    /**
     * 多条件分页查询方法
     */
    @GetMapping("/findByPage")
    public PageResult findByPage(Specification specification, Integer page, Integer rows) {
        try {
            // get请求转码
            if (specification != null && StringUtils.isNoneBlank(specification.getSpecName())) {
                specification.setSpecName(new String(specification.getSpecName().getBytes("ISO8859-1"), "UTF-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        PageResult byPage = specificationService.findByPage(specification, page, rows);
        return byPage;
    }

    /** 保存规格 */
    @PostMapping("/save")
    public boolean save(@RequestBody Specification specification) {
        try {
            specificationService.save(specification);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /** 修改规格 */
    @PostMapping("/update")
    public boolean update(@RequestBody Specification specification) {
        try {
            specificationService.update(specification);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /** 删除规格 */
    @GetMapping("/delete")
    public Boolean delete(Long[] ids){
        try {
            specificationService.deleteAll(ids);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /** 查询所有的规格(id与specName) */
    @GetMapping("/fingBrandList")
    public List<Map<String,Object>> fingBrandList() {
        return specificationService.findAllByIdAndName();
    }
}
