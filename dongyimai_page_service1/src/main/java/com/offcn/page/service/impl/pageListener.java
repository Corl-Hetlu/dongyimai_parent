package com.offcn.page.service.impl;

import com.alibaba.fastjson.JSON;
import com.offcn.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Component
public class pageListener implements MessageListener {

    @Autowired
    private ItemPageService pageService;
    @Override
    public void onMessage(Message message) {

        try {
            TextMessage textMessage=(TextMessage) message;
            String text = textMessage.getText();
            System.out.println("接收到消息："+text);
            boolean b = pageService.genItemHtml(Long.parseLong(text));
            System.out.println("结果："+b);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
