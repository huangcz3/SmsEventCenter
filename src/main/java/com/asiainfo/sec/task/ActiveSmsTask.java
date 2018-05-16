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
 * 10086群发活动 信息处理
 */
@Component
public class ActiveSmsTask {

    private static final Logger logger = LoggerFactory.getLogger(ActiveSmsTask.class);

    private static final String CHANNEL_ID = "d05";
    private static final String CHANNEL_STATUS = "ISPAUSED_" + CHANNEL_ID;
    private static final String CHANNEL_QUOTA = "SEND_QUOTA_" + CHANNEL_ID + "_";
    private static final String CHANNEL_SENDED_CNT = "SENDED_CNT_" + CHANNEL_ID + "_";
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

    @Scheduled(cron = "0 0/30 * * * ?")
    void ScanActiveSmsInfo() {
        ++taskNo;
        String date = DateUtil.getCurrentMonthYYYYMMString();

        //获取当前10086短信渠道是否暂停（N--不暂停，Y--暂停）
        String status = jedisCluster.get(CHANNEL_STATUS);
        if ("N".equals(status)) {
            logger.info("日期：{}，10086群发定时任务启动序号: {}", date, taskNo);

            List<ActiveSmsBean> activeList = smsCenterService.getActiveList(CHANNEL_ID);

            if (activeList.size() > 0)
                activeList.stream().forEach(activeSmsBean -> {

                    //判断mysql的sms_send_info表里是否已有该活动记录
                    int exist = smsCenterService.isExsist(activeSmsBean.getActivityId());
                    if (exist == 0) {

                        logger.info("日期：{}，该活动未传输用户群：{}，状态： {}", date, activeSmsBean.getActivityId(), exist);
                        Long userSize = jedisCluster.scard(REDIS_KEY_USER_LIST + activeSmsBean.getActivityId());

                        //更新该活动的用户量大小,redis中的用户量
                        Map parameterMap = new HashMap<>();
                        String customerCount = String.valueOf(userSize);
                        parameterMap.put("activityId", activeSmsBean.getActivityId());
                        parameterMap.put("customerCount", customerCount);
                        smsCenterService.updateSmsActivitySendInfo(parameterMap);

                        String yyyymm = activeSmsBean.getStartTime().substring(0, 6);
                        //更新地市发送量，返回当前地市可发送量
                        Long sendCnt = updateSendCntHandler.updateSendcnt(CHANNEL_QUOTA + yyyymm, CHANNEL_SENDED_CNT + yyyymm, CHANNEL_ID, activeSmsBean.getCityId(), activeSmsBean.getActivityId(), userSize, 1);

                        if (sendCnt > 0) {
                            //目标用户入库
                            smsCenterHandler.sendActivityUsers(CHANNEL_QUOTA + yyyymm, CHANNEL_SENDED_CNT + yyyymm, activeSmsBean, sendCnt);
                        } else {
                            //超额用户入库
                            extreaHandler.extraUsersHandler(activeSmsBean);
                        }

                        //日志表增加记录
                        smsCenterService.saveSmsActivitySendInfo(activeSmsBean);
                    }
                });
        } else {
            logger.info("10086短息群发(渠道id： {})渠道状态暂停，不进行活动扫描", CHANNEL_ID);
        }
    }

}

