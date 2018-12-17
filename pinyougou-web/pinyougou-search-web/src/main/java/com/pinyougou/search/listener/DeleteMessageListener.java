package com.pinyougou.search.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;

/**
 * 消息监听器(删除商品索引)
 *
 * @author NTP
 * @date 2018/12/13
 */
public class DeleteMessageListener implements SessionAwareMessageListener<ObjectMessage> {
    @Reference(timeout = 10000)
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(ObjectMessage objectMessage, Session session) throws JMSException {
        System.out.println("===========DeleteMessageListener============");
        //接受消息内容
        Long[] goodsIds = (Long[]) objectMessage.getObject();

        //根据goodsIds从索引库中删除SKU商品的索引
        itemSearchService.delete(goodsIds);
    }
}
