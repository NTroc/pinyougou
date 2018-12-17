package com.pinyougou.service;

import com.pinyougou.solr.SolrItem;

import java.util.List;
import java.util.Map; /**
 * @author NTP
 * @date 2018/12/2
 */
public interface ItemSearchService {
    /** 搜索方法 */
    Map<String,Object> seach(Map<String, Object> params);

    /** 添加或修改商品索引 */
    void saveOrUpdate(List<SolrItem> solrItems);

    /** 根据goodsIds从索引库中删除SKU商品的索引 */
    void delete(Long[] goodsIds);
}
