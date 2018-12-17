package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Brand;
import com.pinyougou.service.BrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author NTP
 * @date 2018/11/17
 */
@RestController
@RequestMapping("/brand")
public class BrandController {

    /**
     * 引用服务接口代理对象
     * timeout: 调用服务接口方法超时时间毫秒数
     */
    //@Autowired(required = false)
    @Reference(timeout = 10000)
    private BrandService brandService;

    /**
     * 查询全部品牌
     */
    @GetMapping("/findAll")
    public List<Brand> findAll() {
        return brandService.findAll();
    }


    /**
     * 多条件分页查询品牌
     */
    @GetMapping("/findByPage")
    public PageResult findByPage(Brand brand, Integer page, Integer rows) {
        if (brand != null && StringUtils.isNoneBlank(brand.getName())) {
            try {
                brand.setName(new String(brand.getName().getBytes("ISO8859-1"), "UTF-8"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return brandService.findByPage(brand, page, rows);
    }

    /**
     * 添加品牌
     *
     * @param brand
     * @return
     */
    @PostMapping("/save")
    public boolean save(@RequestBody Brand brand) {
        try {
            brandService.save(brand);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 修改品牌
     *
     * @param brand
     * @return
     */
    @PostMapping("/update")
    public boolean update(@RequestBody Brand brand) {
        try {
            brandService.update(brand);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除品牌
     *
     * @param
     * @return
     */
    @GetMapping("/delete")
    public boolean delete(Long[] ids) {
        try {
            brandService.deleteAll(ids);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 查询所有的品牌(id与name)
     * @return List<Map<Object,String>>
     */
    @GetMapping("/fingBrandList")
    public List<Map<String,Object>> fingBrandList(){
        return brandService.findAllByIdAndName();
    }

}


