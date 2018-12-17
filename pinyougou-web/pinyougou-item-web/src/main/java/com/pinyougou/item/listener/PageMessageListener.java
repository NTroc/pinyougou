package com.pinyougou.item.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.GoodsService;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * 消息监听器(生成静态html页面)
 * @author NTP
 * @date 2018/12/13
 */
public class PageMessageListener implements SessionAwareMessageListener<TextMessage> {

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Reference
    private GoodsService goodsService;
    @Value("${page.dir}")
    private String pageDir;

    @Override
    public void onMessage(TextMessage textMessage, Session session) throws JMSException {
        try {
            System.out.println("=====PageMessageListener======");
            // 获取消息内容
            String goodsId = textMessage.getText();
            System.out.println("goodsId:" + goodsId);
            /** ############### 利用FreeMarker生成商品的静态页面############### */
            // 1. 获取item.ftl模板文件对应的模板对象
            Template template = freeMarkerConfigurer.getConfiguration().getTemplate("item.ftl");
            // 2. 定义模板文件需要的数据模型
            Map<String, Object> dataModel = goodsService.getGoods(Long.valueOf(goodsId));
            // 3. 填充模板输出静态的html页面
            // 创建输出流 http://item.pinyougou.com/129999900.html
            OutputStreamWriter osw = new OutputStreamWriter(
                    new FileOutputStream(pageDir + goodsId + ".html"),"UTF-8");
            // 填充模版生成静态的html页面
            template.process(dataModel,osw);
            // 关闭输出流
            osw.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
