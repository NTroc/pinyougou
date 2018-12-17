package com.pinyougou.item.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.File;

/**
 * 删除静态页面消息监听器
 * @author NTP
 * @date 2018/12/13
 */
public class DeleteMessageListener implements SessionAwareMessageListener<ObjectMessage> {
    @Value("${page.dir}")
    private String pageDir;
    @Override
    public void onMessage(ObjectMessage objectMessage, Session session) throws JMSException {
        System.out.println("==========DeleteMessageListener=============");
        //获取消息内容
        Long[] goodsIds = (Long[]) objectMessage.getObject();
        try {
            for (Long goodsId : goodsIds) {
                File file = new File(pageDir+goodsId+".html");
                //判断文件是否存在
                if (file.exists()){
                    file.delete();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
