package com.pinyougou.sorl.uitl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.Item;
import com.pinyougou.solr.SolrItem;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author NTP
 * @date 2018/12/1
 */
@Component
public class SorlUtils {

    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 查询SKU表中的数据导入到solr服务器的索引库中
     */
    public void importDataToSolr() {
        //1.查询SKU商品数据
        /** 创建Item对象封装查询条件 */
        Item item = new Item();
        //正常的商品
        item.setStatus("1");
        //条件查询
        List<Item> itemList = itemMapper.select(item);
        List<SolrItem> solrItems = new ArrayList<>();
        System.out.println("======开始=======");
        //把Item集合转换为SolrItem集合
        for (Item item1 : itemList) {
            SolrItem solrItem = new SolrItem();
            solrItem.setId(item1.getId());
            solrItem.setTitle(item1.getTitle());
            solrItem.setPrice(item1.getPrice());
            solrItem.setImage(item1.getImage());
            solrItem.setGoodsId(item1.getGoodsId());
            solrItem.setCategory(item1.getCategory());
            solrItem.setBrand(item1.getBrand());
            solrItem.setSeller(item1.getSeller());
            solrItem.setUpdateTime(item1.getUpdateTime());

            String spec = item1.getSpec();
            /** 将spec字段的json字符串转换成map */
            Map<String, String> specMap = JSON.parseObject(item1.getSpec(),Map.class);
            /** 设置动态域 */
            solrItem.setSpecMap(specMap);

            solrItems.add(solrItem);
        }
        //添加索引到solr服务器索引库中
        UpdateResponse updateResponse = solrTemplate.saveBeans(solrItems);
        if (updateResponse.getStatus() == 0) {
            solrTemplate.commit();
        }else {
            solrTemplate.rollback();
        }
        System.out.println("======结束=======");
    }


    public static void main(String[] args) {
        //获取Spring容器
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        //获取bean
        SorlUtils sorlUtils = ac.getBean(SorlUtils.class);

        sorlUtils.importDataToSolr();
    }
}
