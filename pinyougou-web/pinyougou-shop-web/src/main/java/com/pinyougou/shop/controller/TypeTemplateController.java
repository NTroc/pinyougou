package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TypeTemplate;
import com.pinyougou.service.TypeTemplateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 类型模板控制器
 *
 * @author NTP
 * @date 2018/11/24
 */
@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {
    @Reference(timeout = 1000)
    private TypeTemplateService typeTemplateService;

    /** 根据模板类型查改模板信息 */
    @GetMapping("/findOne")
    public TypeTemplate findOne(Long id) {
        return typeTemplateService.findOne(id);
    }

    /** 查询该模版对应的规格与规格选项 */
    @GetMapping("/findSpecByTemplateId")
    public List<Map> findSpecByTemplateId(Long id) {
        return typeTemplateService.findSpecByTemplateId(id);
    }
}


