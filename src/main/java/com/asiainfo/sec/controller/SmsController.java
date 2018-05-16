package com.asiainfo.sec.controller;

import com.asiainfo.sec.handler.SendedCntHandler;
import com.asiainfo.sec.service.InitService;
import com.asiainfo.sec.util.DateUtil;
import com.asiainfo.sms.client.Client;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.JedisCluster;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by PuMg on 2017/10/30/0030.
 */
@RestController
@RequestMapping("/sms")
public class SmsController {

    @Autowired
    private InitService initService;

    @Autowired
    private JedisCluster jedisCluster;

    @Autowired
    @Qualifier("smsClient")
    private Client smsClient;

    @Autowired
    @Qualifier("yhtxClient")
    private Client yhtxClient;

    @Autowired
    private SendedCntHandler sendedCntHandler;

    private String CHANNEL_QUOTA_YHTX = "SEND_QUOTA_DAY_d01_" + DateUtil.getCurrentMonthYYYYMMString();
    private String CHANNEL_QUOTA_10086 = "SEND_QUOTA_d05_" + DateUtil.getCurrentMonthYYYYMMString();


    @GetMapping("/test")
    public Map test(){
        for (int i =0 ;i<500000;i++){

                jedisCluster.sadd("SET-USERLIST:A1514467635854","137001"+String.format("%05d",i));
                jedisCluster.sadd("SET-USERLIST:A1514467594050","137002"+String.format("%05d",i));
                jedisCluster.sadd("SET-USERLIST:A1514467557669","137003"+String.format("%05d",i));
                jedisCluster.sadd("SET-USERLIST:A1514467538221","137004"+String.format("%05d",i));
                jedisCluster.sadd("SET-USERLIST:A1514467434621","137005"+String.format("%05d",i));
                jedisCluster.sadd("SET-USERLIST:A1514467406905","137006"+String.format("%05d",i));
                jedisCluster.sadd("SET-USERLIST:A1514467374396","137007"+String.format("%05d",i));
                jedisCluster.sadd("SET-USERLIST:A1514467349249","137008"+String.format("%05d",i));
                jedisCluster.sadd("SET-USERLIST:A1514467320071","137009"+String.format("%05d",i));
                jedisCluster.sadd("SET-USERLIST:A1514467284197","137010"+String.format("%05d",i));

        }



        return initService.getChannelQuota();
    }

    /**
     * 白名单批量发送
     * @param channelId 渠道id
     * @param msg     发送内容
     * @param phones  发送列表以","分隔
     * @param subPort 发送端口 默认0
     * @param single  0群发 1单发
     * @return
     */
    @GetMapping("/sendWhiteSms")
    public Map sendWhiteSms(@Param("channelId")String channelId,@Param("actId")String actId,@Param("msg")String msg,@Param("phones")String phones,@RequestParam(value = "subPort",defaultValue = "0") String subPort,@Param("single") int single){
        Map resultMap = new HashMap<>();
        try {
            List whiteList = Arrays.asList(phones.split(","));
            if("d05".equals(channelId)){
                smsClient.whiteListBatchSend(actId,whiteList,msg,subPort);
            }else{
                yhtxClient.whiteListBatchSend(actId,whiteList,msg,subPort);
            }
            resultMap.put("flag","0");
            resultMap.put("msg","successful!");
        }catch (Exception e){
            resultMap.put("flag","-1");
            resultMap.put("msg",e.getMessage().toString());
        }
        return resultMap;
    }

    /**
     * 白名单单个发送
     * @param channelId 渠道id
     * @param msg     发送内容
     * @param phones  发送列表以","分隔
     * @param subPort 发送端口 默认0
     * @param single  0群发 1单发
     * @return
     */
    @GetMapping("/sendWhiteSingle")
    public Map sendWhiteSingle(@RequestParam("channelId")String channelId, @RequestParam("actId")String actId, @RequestParam("msg")String msg, @RequestParam("phone")String phones, @RequestParam(value = "subPort",defaultValue = "0") String subPort, @RequestParam("single") int single){
        Map resultMap = new HashMap<>();
        try {
            Map map = new HashMap<>();
            map.put(phones,msg);
            if("d05".equals(channelId)){
                smsClient.whiteListSingleSend(actId,map,subPort);
            }else{
                yhtxClient.whiteListSingleSend(actId,map,subPort);
            }
            resultMap.put("flag","0");
            resultMap.put("msg","successful!");
        }catch (Exception e){
            resultMap.put("flag","-1");
            resultMap.put("msg",e.getMessage().toString());
        }
        return resultMap;
    }

    /**
     * 更新地市配额
     * @param channelId
     * @return
     */
    @GetMapping("/updateQuato")
    public String updateQuato(@RequestParam("channelId") String channelId){

        String quotaKey = "";
        if (channelId.equals("d05")){
            quotaKey = CHANNEL_QUOTA_10086;
        }
        if (channelId.equals("d01")){
            quotaKey = CHANNEL_QUOTA_YHTX;
        }
        sendedCntHandler.updateQuato(channelId,quotaKey);

        return "success";
    }

}
