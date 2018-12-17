package com.pinyougou.search.listener;

/**
 * 商品消息监听器
 * @author NTP
 * @date 2018/12/12
 */

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.Item;
import com.pinyougou.service.GoodsService;
import com.pinyougou.service.ItemSearchService;
import com.pinyougou.solr.SolrItem;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ItemMessageListener implements SessionAwareMessageListener<ObjectMessage> {
    @Reference(timeout = 10000)
    private GoodsService goodsService;
    @Reference(timeout = 10000)
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(ObjectMessage objectMessage, Session session) throws JMSException {
        System.out.println("===ItemMessageListener===");
        // 获取消息内容
        Long[] ids = (Long[])objectMessage.getObject();
        System.out.println("ids:" + Arrays.toString(ids));
        // 查询上架的SKU商品数据
        List<Item> itemList = goodsService.findItemByGoodsId(ids);
        // 判断集合
        if (itemList.size() > 0){
            // 把List<Item>转化成List<SolrItem>
            List<SolrItem> solrItems = new ArrayList<>();
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
            // 把SKU商品数据同步到索引库
            itemSearchService.saveOrUpdate(solrItems);
        }
    }
}
