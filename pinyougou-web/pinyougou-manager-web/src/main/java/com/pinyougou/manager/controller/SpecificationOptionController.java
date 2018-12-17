package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.SpecificationOption;
import com.pinyougou.service.SpecificationOptionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 规格选项控制器
 * @author NTP
 * @date 2018/11/24
 */
@RestController
@RequestMapping("/specificationOption")
public class SpecificationOptionController {
    @Reference(timeout = 10000)
    private SpecificationOptionService specificationOptionService;

    /**
     * 根据规格主键查询规格选项
     * @param specId
     * @return List<SpecificationOption>
     */
    @GetMapping("/findSpecOption")
    public List<SpecificationOption> findSpecOption(Long specId) {
        return specificationOptionService.findSpecOption(specId);
    }
}
