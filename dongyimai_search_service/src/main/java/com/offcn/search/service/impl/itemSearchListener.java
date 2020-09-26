package com.offcn.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

@Component
public class itemSearchListener implements MessageListener {

    @Autowired
    private ItemSearchService searchService;
    @Override
    public void onMessage(Message message) {
        System.out.println("监听接收到消息...");
        
        try {
            TextMessage textMessage= (TextMessage) message;
            String text = textMessage.getText();
            List<TbItem> tbItems = JSON.parseArray(text, TbItem.class);
            for (TbItem item : tbItems) {
                System.out.println(item.getId()+" "+item.getTitle());
                Map specMap = JSON.parseObject(item.getSpec());
                item.setSpecMap(specMap);
            }
            searchService.importList(tbItems);
            System.out.println("成功导入到索引库");
        } catch (JMSException e) {
            e.printStackTrace();
        }
       
    }
}
