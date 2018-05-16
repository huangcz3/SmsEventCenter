package com.asiainfo.sec.handler;


import com.asiainfo.sec.entity.ActiveSmsBean;
import com.asiainfo.sec.model.ConstantPojo;
import com.asiainfo.sec.service.SMSCenterService;
import com.asiainfo.sec.util.DateUtil;
import com.asiainfo.sec.util.FilterWhiteListUtil;
import com.asiainfo.sms.api.bean.SendFrequency;
import com.asiainfo.sms.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 短信中心事件处理类
 */
@Component
public class SmsCenterHandler {

    private static final Logger logger = LoggerFactory.getLogger(SmsCenterHandler.class);

    @Autowired
    SMSCenterService smsCenterService;

    @Autowired
    JmsMessagingTemplate jmsTemplate;

    @Autowired
    private JedisCluster jedisCluster;

    @Autowired
    private UpdateSendCntHandler updateSendCntHandler;

    @Autowired
    private FilterWhiteListUtil filterWhiteListUtil;

    @Autowired
    private RpcHandler rpcHandler;

    @Autowired
    @Qualifier("smsClient")
    private Client smsClient;

    @Autowired
    @Qualifier("yhtxClient")
    private Client yhtxClient;

    @Autowired
    @Qualifier("sendFrequency")
    private SendFrequency sendFrequency;

    @Autowired
    private ExtreaHandler extreaHandler;

    private static int no = 0;

    /**
     * @description:将目标用户入库
     * @param quotaKey       渠道配额key
     * @param cntKey         渠道已发送量key
     * @param activeSmsBean  活动bean
     * @param sendCnt        当前地市可发送量
     */
    @Async("sendAsync")
    public void sendActivityUsers(String quotaKey,String cntKey,ActiveSmsBean activeSmsBean, Long sendCnt) {

        synchronized(this) {
            logger.info("异步方法处理调用开始===>{}，当前线程id===>{}，当前线程名===>{}",++no,Thread.currentThread().getId(),Thread.currentThread().getName());

            //设置延时策略。判断活动执行周期是否大于1，大于1则设置延时策略，不大于1则不设置
            int intervalTime = DateUtil.getDateSpace(activeSmsBean.getEndTime(),activeSmsBean.getStartTime());
            if (intervalTime > 1){
                sendFrequency.setDelayed_send(1);//延时发送
            }
            else {
                sendFrequency.setDelayed_send(0);//不延时发送
            }
            sendFrequency.setId_no(activeSmsBean.getActivityId());

            if (activeSmsBean.getChannelId().equals("d05")){
                smsClient.setPlot(sendFrequency);
            }
        }

        String activityId = activeSmsBean.getActivityId();
        long whiteCnt = 0;
        long userCnt = 0;
        long readRows = Long.valueOf(0);
        List<String> extraList = new ArrayList<>();

        //从redis中读取用户群，通过sscan命令扫描，cursor游标进行标识，每次扫描最大用户数ConstantPojo.READ_MAX_ROWS
        String cursor = "0";
        ScanResult<String> scan;
        ScanParams scanParams = new ScanParams();
        scanParams.count( ConstantPojo.READ_MAX_ROWS);
        do {
            scan = jedisCluster.sscan(ConstantPojo.REDIS_KEY_USER_LIST + activityId,cursor,scanParams);
            List<String> result = scan.getResult();
            userCnt += result.size();
/*            for (String phoneNo : result) {
                SmsBean smsBean = new SmsBean();
                smsBean.setPhoneNo(phoneNo);
                //获取短息营销用语
                String sendMsg = jedisCluster.get("MS:"+phoneNo+":"+activityId);
                smsBean.setSendMsg(sendMsg);
                smsBeanList.add(smsBean);
            }*/
            cursor = scan.getStringCursor();

            if(result.size() >0){
                //过滤白名单用户，获取过滤后的newList即为当前待发送用户群。先处理目标用户，后处理白名单用户
                Map fileterMap = filterWhiteListUtil.filterWhiteListAll(result);

                //处理目标用户
                List<String> userList = (List<String>) fileterMap.get("userList");
                List<String> newList = new ArrayList(new HashSet(userList));
                readRows += newList.size();
                Long sendSize ;
                List<String> sendList ;

                //如果读取用户数大于当前可发送量，截断部分用户，取0-----sendSize的用户数
                if(readRows > sendCnt){
                    sendSize = sendCnt - (readRows - newList.size());//当前可发送量-已推送至rpc的量
                    if(sendSize > 0){
                        sendList = newList.subList(0,sendSize.intValue());
                        rpcHandler.sendUsers(activeSmsBean,sendList,0);

                        //剩余未发送量压入超额list
                        extraList.addAll(newList.subList(sendSize.intValue(),newList.size()));
                    }else{
                        extraList.addAll(newList);
                    }
                }else{
                    sendList = newList;
                    rpcHandler.sendUsers(activeSmsBean,sendList,0);
                }
                //处理白名单用户
                List<String> whiteList = (List<String>) fileterMap.get("whiteList");
                List<String> newWhiteList = new ArrayList(new HashSet(whiteList));
                whiteCnt += newWhiteList.size();
                for(int i=0;i<newWhiteList.size();i+=30){
                    int size = i+30 > newWhiteList.size() ? newWhiteList.size() : i+30;
                    List<String> tmpList = newWhiteList.subList(i,size);
                    rpcHandler.sendWhiteList(activeSmsBean,tmpList,"0");
                }
            }

            //休眠几秒，等待rpc返回结果
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } while (!cursor.equals("0") );

        if(extraList.size()>0){
            extreaHandler.extraUserList(activeSmsBean.getActivityId(),extraList);
        }

        logger.info("活动：{}，redis中目标用户读取结束，用户量：{},白名单用户量：{}",activityId,userCnt,whiteCnt);
        updateSendCntHandler.updateSendcnt(quotaKey,cntKey,activeSmsBean.getChannelId(),activeSmsBean.getCityId(),activeSmsBean.getActivityId(),whiteCnt,2);

        //更新该活动日志表，从redis中目标用户读取数
        Map parameterMap = new HashMap<>();
        String recordedCount = String.valueOf(userCnt + extraList.size());
        parameterMap.put("activityId",activeSmsBean.getActivityId());
        parameterMap.put("recordedCount",recordedCount);
        smsCenterService.updateSmsActivitySendInfo(parameterMap);
    }

}
