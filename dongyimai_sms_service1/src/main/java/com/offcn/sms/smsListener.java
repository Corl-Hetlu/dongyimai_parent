package com.offcn.sms;

import com.aliyuncs.CommonResponse;
import com.aliyuncs.exceptions.ClientException;
import com.offcn.util.SmsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

@Component("smsListener")
public class smsListener implements MessageListener {
    @Autowired
    private SmsUtil smsUtil;

    @Override
    public void onMessage(Message message) {
        if(message instanceof MapMessage){

            MapMessage map=(MapMessage)message;

            try {
                System.out.println("收到短信发送请求---》mobile:"+map.getString("mobile")+"  param:"+map.getString("param"));
                smsUtil.sendSms(map.getString("mobile"), map.getString("param"));




            } catch (ClientException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
        }

    }

}
