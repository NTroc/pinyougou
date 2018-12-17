package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.service.ItemSearchService;
import com.pinyougou.solr.SolrItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author NTP
 * @date 2018/12/2
 */
@Service(interfaceName = "com.pinyougou.service.ItemSearchService")
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 搜索方法
     *
     * @param params
     */
    @Override
    public Map<String, Object> seach(Map<String, Object> params) {
        /** 创建Map集合封装返回数据 **/
        Map<String, Object> data = new HashMap<>();
        /*获取检索关键字*/
        String keywords = (String) params.get("keywords");

        //获取当前页码
        Integer page = (Integer) params.get("page");
        if (page == null || page < 1) {
            //默认当前页码
            page = 1;
        }
        //获取当前页大小
        Integer rows = (Integer) params.get("rows");
        if (rows == null || page < 1) {
            //默认当前页大小
            rows = 20;
        }

        //判断检索关键字
        if (StringUtils.isNoneBlank(keywords)) {
            /** 创建高亮查询对象 */
            HighlightQuery query = new SimpleHighlightQuery();
            /** 创建高亮选项对象 */
            HighlightOptions highlightOptions = new HighlightOptions();
            // 设置哪个Field中出现关键字需要高亮
            highlightOptions.addField("title");
            // <font color='red'>iphone</font>
            // 设置高亮格式器前缀(在关键字前面加html标签)
            highlightOptions.setSimplePrefix("<font color='red'>");
            // 设置高亮格式器后缀(在关键字后面加html标签)
            highlightOptions.setSimplePostfix("</font>");
            // 添加高亮选项参数对象
            query.setHighlightOptions(highlightOptions);
            // 创建条件对象 keywords(复制域)
            Criteria criteria = new Criteria("keywords").is(keywords);
            //添加查询条件
            query.addCriteria(criteria);

            // {keywords: "", category: "手机", brand: "三星", price: "1500-2000",
            // spec: {网络: "电信3G", 机身内存: "128G"}}
            // 按商品分类过滤
            String category = (String) params.get("category");
            if (StringUtils.isNoneBlank(category)) {
                // 过滤条件
                Criteria criteria1 = new Criteria("category").is(category);
                // 添加过滤查询
                query.addFilterQuery(new SimpleFilterQuery(criteria1));
            }

            // 按商品品牌过滤
            String brand = (String) params.get("brand");
            if (StringUtils.isNoneBlank(brand)) {
                // 过滤条件
                Criteria criteria1 = new Criteria("brand").is(brand);
                // 添加过滤查询
                query.addFilterQuery(new SimpleFilterQuery(criteria1));
            }

            // 按规格过滤 spec: {网络: "电信3G", 机身内存: "128G"}
            Map<String, String> specMap = (Map<String, String>) params.get("spec");
            if (specMap != null && specMap.size() > 0) {
                // 迭代规格
                for (String key : specMap.keySet()) {
                    // 过滤条件 spec_*
                    Criteria criteria1 = new Criteria("spec_" + key).is(specMap.get(key));
                    // 添加过滤查询
                    query.addFilterQuery(new SimpleFilterQuery(criteria1));
                }
            }

            // 按价格区间过滤 0-500 1000-1500 3000-*
            String price = (String) params.get("price");
            if (StringUtils.isNoneBlank(price)) {
                // 得到价格区间数组
                String[] priceArr = price.split("-");

                // 判断起始价格是不是零，不是零添加过滤条件
                if (!"0".equals(priceArr[0])) {
                    // 过滤条件 price >= ?
                    Criteria criteria1 = new Criteria("price").greaterThanEqual(priceArr[0]);
                    // 添加过滤查询
                    query.addFilterQuery(new SimpleFilterQuery(criteria1));
                }

                // 判断结束价格是不是星号，不是星号添加过滤条件
                if (!"*".equals(priceArr[1])) {
                    // 过滤条件 price <= ?
                    Criteria criteria1 = new Criteria("price").lessThanEqual(priceArr[1]);
                    // 添加过滤查询
                    query.addFilterQuery(new SimpleFilterQuery(criteria1));
                }
            }

            // 添加排序代码
            String sortField = (String) params.get("sortField");
            String sortValue = (String) params.get("sort");
            if (StringUtils.isNoneBlank(sortField) && StringUtils.isNoneBlank(sortValue)) {
                // 添加排序
                query.addSort(new Sort("ASC".equals(sortValue) ? Sort.Direction.ASC : Sort.Direction.DESC, sortField));
            }

            /** 设置起始记录查询数 */
            query.setOffset((page - 1) * rows);
            /** 设置每页显示记录数 */
            query.setRows(rows);

            //高亮分页查询
            HighlightPage<SolrItem> highlightPage = solrTemplate.queryForHighlightPage(query, SolrItem.class);
            // 获取高亮选项集合
            List<HighlightEntry<SolrItem>> highlighted = highlightPage.getHighlighted();
            // 迭代高亮选项集合
            for (HighlightEntry<SolrItem> highlightEntry : highlighted) {
                // 获取文档对应的实体
                SolrItem solrItem = highlightEntry.getEntity();
                // 获取高亮内容集合
                List<HighlightEntry.Highlight> highlights = highlightEntry.getHighlights();
                // 判断集合是否为空
                if (highlights != null && highlights.size() > 0) {
                    // 获取标题的高亮内容
                    String title = highlights.get(0).getSnipplets().get(0);
                    // 设置高亮后的标题容
                    solrItem.setTitle(title);
                }
            }
            /** 获取总页数 */
            data.put("totalPages", highlightPage.getTotalPages());
            /** 获取总记录数 */
            data.put("total", highlightPage.getTotalElements());
            /** 获取内容 */
            data.put("rows", highlightPage.getContent());
        } else {
            /** 创建查询对象 */
            Query query = new SimpleQuery("*:*");

            /** 设置起始记录查询数 */
            query.setOffset((page - 1) * rows);
            /** 设置每页显示记录数 */
            query.setRows(rows);

            /** 分页检索 */
            ScoredPage<SolrItem> solrPage = solrTemplate.queryForPage(query, SolrItem.class);
            /** 获取总页数 */
            data.put("totalPages", solrPage.getTotalPages());
            /** 获取总记录数 */
            data.put("total", solrPage.getTotalElements());
            /** 获取内容 */
            data.put("rows", solrPage.getContent());
        }
        return data;
    }

    /**
     * 添加或修改商品索引
     */
    public void saveOrUpdate(List<SolrItem> solrItems) {
        UpdateResponse updateResponse = solrTemplate.saveBeans(solrItems);
        if (updateResponse.getStatus() == 0) {
            //提交事务
            solrTemplate.commit();
        } else {
            //回滚事务
            solrTemplate.rollback();
            ;
        }
    }

    /**
     * 根据goodsIds从索引库中删除SKU商品的索引
     */
    @Override
    public void delete(Long[] goodsIds) {
        try {
            // 创建查询对象
            Query query = new SimpleQuery();
            // 创建条件对象
            Criteria criteria = new Criteria("goodsId").in(Arrays.asList(goodsIds));
            // 添加条件
            query.addCriteria(criteria);
            // 条件删除
            UpdateResponse updateResponse = solrTemplate.delete(query);
            if (updateResponse.getStatus() == 0) {
                //提交事务
                solrTemplate.commit();
            } else {
                //回滚事务
                solrTemplate.rollback();
                ;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
