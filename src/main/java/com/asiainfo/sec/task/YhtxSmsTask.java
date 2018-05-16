package com.asiainfo.sec.task;

import com.asiainfo.sec.entity.ActiveSmsBean;
import com.asiainfo.sec.handler.ExtreaHandler;
import com.asiainfo.sec.handler.SmsCenterHandler;
import com.asiainfo.sec.handler.UpdateSendCntHandler;
import com.asiainfo.sec.service.SMSCenterService;
import com.asiainfo.sec.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * 优惠提醒活动 信息处理
 *
 */
@Component
public class YhtxSmsTask {

    private static final Logger logger = LoggerFactory.getLogger(YhtxSmsTask.class);

    private static final String CHANNEL_ID = "d01";
    private static final String CHANNEL_STATUS = "ISPAUSED_"+CHANNEL_ID;
    private static final String CHANNEL_QUOTA = "SEND_QUOTA_DAY_"+CHANNEL_ID+"_";
    private static final String CHANNEL_SENDED_CNT = "SENDED_CNT_DAY_"+CHANNEL_ID+"_";
    private static final String REDIS_KEY_USER_LIST = "SET-USERLIST:";

    @Autowired
    private JedisCluster jedisCluster;

    @Autowired
    private SMSCenterService smsCenterService;

    @Autowired
    private UpdateSendCntHandler updateSendCntHandler;

    @Autowired
    private SmsCenterHandler smsCenterHandler;

    @Autowired
    private ExtreaHandler extreaHandler;

    private static int taskNo = 0;

    @Scheduled(cron = "0 0/15 * * * ?")
    void ScanActiveSmsInfo(){
        ++taskNo;
        String preDate = DateUtil.getCurrentMonthYYYYMMString();
        //获取当前优惠提醒短信渠道是否暂停
        String status = jedisCluster.get(CHANNEL_STATUS);
        if("N".equals(status)){
            logger.info("日期：{},优惠提醒定时任务启动序号: {}",preDate,taskNo);

            List<ActiveSmsBean> activeList = smsCenterService.getActiveList(CHANNEL_ID);
            if (activeList.size() > 0)
               for(ActiveSmsBean sms : activeList){
                    int exsist = smsCenterService.isExsist(sms.getActivityId());

                    if (exsist == 0) {
                        logger.info("日期：{}，该活动未传输用户群：{}，状态： {}",preDate,sms.getActivityId(),exsist);

                        //日志表增加记录
                        smsCenterService.saveSmsActivitySendInfo(sms);

                        Long userSize = jedisCluster.scard(REDIS_KEY_USER_LIST+sms.getActivityId());

                        //更新该活动的用户量大小,redis中的用户量
                        Map parameterMap = new HashMap<>();
                        String customerCount = String.valueOf(userSize);
                        parameterMap.put("activityId",sms.getActivityId());
                        parameterMap.put("customerCount",customerCount);
                        smsCenterService.updateSmsActivitySendInfo(parameterMap);

                        Long sendCnt = updateSendCntHandler.updateSendcnt(CHANNEL_QUOTA+sms.getStartTime(),CHANNEL_SENDED_CNT+sms.getStartTime(),CHANNEL_ID,sms.getCityId(),sms.getActivityId(),userSize,1);

                        if(sendCnt > 0){
                            smsCenterHandler.sendActivityUsers(CHANNEL_QUOTA+sms.getStartTime(),CHANNEL_SENDED_CNT+sms.getStartTime(),sms,sendCnt);
                        }else{
                            extreaHandler.extraUsersHandler(sms);
                        }

                    }

                }
        }else{
            logger.info("定时任务启动序号: {},渠道状态暂停，不进行活动扫描",taskNo);
        }
    }
}
