package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.TypeTemplate;
import com.pinyougou.service.TypeTemplateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author NTP
 * @date 2018/11/22
 */
@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {

    @Reference(timeout = 10000)
    private TypeTemplateService typeTemplateService;

    @GetMapping("/findByPage")
    public PageResult findByPage(TypeTemplate typeTemplate,Integer page,Integer rows){
        try {
            //// get请求转码
            if (typeTemplate!=null && StringUtils.isNoneBlank(typeTemplate.getName())){
                typeTemplate.setName(new String(typeTemplate.getName().getBytes("ISO8859-1"),"UTF-8"));
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return typeTemplateService.findByPage(typeTemplate,page,rows);
    }

    /*添加模板类型*/
    @PostMapping("/save")
    public boolean save(@RequestBody TypeTemplate typeTemplate){
        try {
            typeTemplateService.save(typeTemplate);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }


    /*修改模板类型*/
    @PostMapping("/update")
    public boolean update(@RequestBody TypeTemplate typeTemplate){
        try {
            typeTemplateService.update(typeTemplate);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /*删除类型模板*/
    @GetMapping("/delete")
    public boolean delete(Long[] ids){
        try {
            typeTemplateService.deleteAll(ids);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 查询所有的类型模板(id与name)
     * @return List<Map<String,Object>>
     */
    @GetMapping("/findTypeTemplateList")
    public List<Map<String,Object>> findTypeTemplateList(){
        return typeTemplateService.findAllByIdAndName();
    }
}

