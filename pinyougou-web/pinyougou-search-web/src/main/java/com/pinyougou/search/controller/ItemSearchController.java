package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.ItemSearchService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author NTP
 * @date 2018/12/2
 */
@RestController
public class ItemSearchController {

    @Reference(timeout = 30000)
    private ItemSearchService itemSearchService;


    @PostMapping("/Search")
    public Map<String, Object> search(@RequestBody Map<String, Object> params) {
        return itemSearchService.seach(params);
    }
}
