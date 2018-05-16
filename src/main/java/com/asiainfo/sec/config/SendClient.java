package com.asiainfo.sec.config;

import com.asiainfo.sms.api.bean.SendFrequency;
import com.asiainfo.sms.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 获取活动目标用户
 * Created by PuMg on 2017/11/30/0030.
 */

@Configuration
public class SendClient {

    private static final Logger logger = LoggerFactory.getLogger(SendClient.class);

    @Value("${sec.sms.init.10086.businessId}")
    private String smsBusinessId;

    @Value("${sec.sms.init.10086.key}")
    private String smsKey;

    @Value("${sec.sms.init.yhtx.businessId}")
    private String yhtxBusinessId;

    @Value("${sec.sms.init.yhtx.key}")
    private String yhtxKey;



    @Bean(name = "yhtxClient" )
    public Client yhtxClient(){
        Client  yhtxClient = new Client();
        yhtxClient.init(yhtxKey,yhtxBusinessId);
        return yhtxClient;
    }

    @Bean(name = "smsClient" )
    public Client init(){
        Client  client = new Client();
        client.init(smsKey,smsBusinessId);
        return client;
    }

    /**
     * 设置默认发送策略
     */
    @Bean(name = "sendFrequency")
    public SendFrequency initSendFrequency(){
        SendFrequency sendFrequency = new SendFrequency();
        sendFrequency.setBusSetting(false);
        sendFrequency.setSend_interval(15);
        sendFrequency.setSend_period(1035776);
        sendFrequency.setDelayed_send(1);
        sendFrequency.setSend_times(2);
        sendFrequency.setDay_max_count(1);
        return sendFrequency;

    }

}
