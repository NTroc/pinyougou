package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Goods;
import com.pinyougou.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.security.krb5.internal.crypto.Des;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * 商品上下架控制器
 *
 * @author NTP
 * @date 2018/11/24
 */
@RestController
@RequestMapping("/goodsMarketable")
public class GoodsMarketableController {
    @Reference(timeout = 10000)
    private GoodsService goodsService;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination solrQueue; // 生成索引队列
    @Autowired
    private Destination solrDeleteQueue; // 删除索引队列
    @Autowired
    private Destination pageTopic;
    @Autowired
    private Destination pageDeleteTopic;



    /**
     * 分页查询
     *
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("/findByPage")
    public PageResult findByPage(Integer page, Integer rows) {
        //封装查询条件
        Goods goods = new Goods();
        /** 获取登录商家编号 */
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        /** 添加查询条件 */
        goods.setSellerId(sellerId);
        goods.setAuditStatus("1");

        return goodsService.findByPage(goods, page, rows);
    }

    /**
     * 商家商品上下架(修改可销售状态)
     */
    @GetMapping("/updateMarketable")
    public boolean updateMarketable(Long[] ids, String status) {
        try {
            goodsService.updateStatus("is_marketable", ids, status);
            // 判断商品上下架
            if ("1".equals(status)) { // 商品上架
                // 1. 发送消息到消息服务器，创建该商品的索引
                jmsTemplate.send(solrQueue, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        // 创建消息的内容体 ids : 多个goodsId(tb_goods表的主键列)
                        return session.createObjectMessage(ids);
                    }
                });

                // 2. 发送消息到消息服务器，生成该商品静态的html页面
                for (Long goodsId : ids) {
                    jmsTemplate.send(pageTopic, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            return session.createTextMessage(goodsId.toString());
                        }
                    });
                }
            } else {// 商品下架
                // 1. 发送消息到消息服务器，删除该商品的索引
                jmsTemplate.send(solrDeleteQueue, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        // 创建消息的内容体 ids : 多个goodsId(tb_goods表的主键列)
                        return session.createObjectMessage(ids);
                    }
                });

                // 2. 发送消息到消息服务器，删除该商品的静态页面
                jmsTemplate.send(pageDeleteTopic, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        // 创建消息的内容体 ids : 多个goodsId(tb_goods表的主键列)
                        return session.createObjectMessage(ids);
                    }
                });
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

}
