package com.offcn.page.service.impl;

import com.offcn.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;

@Component
public class pageDeleteListener implements MessageListener {
    @Autowired
    private ItemPageService pageService;

    @Override
    public void onMessage(Message message) {

        try {
            ObjectMessage objectMessage=(ObjectMessage) message;
            Long[] goodIds = (Long[]) objectMessage.getObject();
            boolean b = pageService.deleteItemHtml(goodIds);
            System.out.println("网页删除结果："+b);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
