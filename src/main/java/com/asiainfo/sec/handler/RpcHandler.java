package com.asiainfo.sec.handler;

import com.asiainfo.sec.entity.ActiveSmsBean;
import com.asiainfo.sms.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by PuMg on 2017/12/26/0026.
 */
@Component
public class RpcHandler {


    private static final Logger logger = LoggerFactory.getLogger(RpcHandler.class);

    @Autowired
    @Qualifier("smsClient")
    private Client smsClient;

    @Autowired
    @Qualifier("yhtxClient")
    private Client yhtxClient;



    /**
     * 目标用户推送
     */
    @Async
    public void sendUsers(ActiveSmsBean activeSmsBean, List phones,int state) {
        String result = "";
        if("d05".equals(activeSmsBean.getChannelId())){
            result = smsClient.batchSend(activeSmsBean.getActivityId(), activeSmsBean.getStartTime(), activeSmsBean.getEndTime(), phones, activeSmsBean.getSmsContent(), "0", activeSmsBean.getCityId(), state,1);
        }else{

            result =  yhtxClient.batchSend(activeSmsBean.getActivityId(), activeSmsBean.getStartTime(), activeSmsBean.getEndTime(), phones, activeSmsBean.getSmsContent(), "0", activeSmsBean.getCityId(), state,1);
        }
        logger.info("rpc 返回结果：{}",result);
    }

    /**
     * 白名单推送
     */
    @Async
    public void sendWhiteList(ActiveSmsBean activeSmsBean,List phones, String subPort){
        String result;
        if("d05".equals(activeSmsBean.getChannelId())){
            result = smsClient.whiteListBatchSend(activeSmsBean.getActivityId(), phones, activeSmsBean.getSmsContent(), subPort);
        }else {
            result = yhtxClient.whiteListBatchSend(activeSmsBean.getActivityId(), phones, activeSmsBean.getSmsContent(), subPort);
        }
        logger.info("rpc 白名单发送结果：",result);
    }

}
