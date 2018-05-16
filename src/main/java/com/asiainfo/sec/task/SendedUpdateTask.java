package com.asiainfo.sec.task;

import com.asiainfo.sec.handler.SendedCntHandler;
import com.asiainfo.sec.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by PuMg on 2017/12/26/0026.
 */
@Component
public class SendedUpdateTask {

    private static final Logger logger = LoggerFactory.getLogger(SendedUpdateTask.class);

    @Autowired
    SendedCntHandler sendedCntHandler;

    private String CHANNEL_ID_10086 = "d05";
    private String CHANNEL_QUOTA_10086_PREFIX = "SEND_QUOTA_" + CHANNEL_ID_10086 + "_";
    private String CHANNEL_SENDED_CNT_10086_PREFIX = "SENDED_CNT_" + CHANNEL_ID_10086 + "_";

    private String CHANNEL_ID_YHTX = "d01";
    private String CHANNEL_QUOTA_YHTX_PREFIX = "SEND_QUOTA_DAY_" + CHANNEL_ID_YHTX + "_";
    private String CHANNEL_SENDED_CNT_YHTX_PREFIX = "SENDED_CNT_DAY_" + CHANNEL_ID_YHTX + "_";

    //读取10086地市配额，更新到redis中 月初更新06：00
    @Scheduled(cron = "0 0 6 1 * ?")
    void updateQuato10086() {
        //sendedCntHandler.updateQuato(CHANNEL_ID_10086, CHANNEL_QUOTA_10086);
        sendedCntHandler.updateQuatoByMonthDay(-1, CHANNEL_QUOTA_10086_PREFIX + DateUtil.getCurrentMonthYYYYMMString(), CHANNEL_ID_10086, 2);
    }

    //读取优惠提醒地市配额，更新到redis 月初更新06：00
    @Scheduled(cron = "0 0 6 1 * ?")
    void updateQuatoYhtx() {
        //sendedCntHandler.updateQuato(CHANNEL_ID_YHTX, CHANNEL_QUOTA_YHTX);
        sendedCntHandler.updateQuatoByMonthDay(DateUtil.getDaysOfMonth(new Date()), CHANNEL_QUOTA_YHTX_PREFIX + DateUtil.getCurrentMonthYYYYMMString(), CHANNEL_ID_YHTX, 1);
    }

    //每隔1小时 刷新redis中的地市发送量 读取mysql 日表中的 记录 按地市分组更新  7：30 —— 21：00 相减
    @Scheduled(cron = "0 0 0/1 * * ?  ")
    void updateCitySendCnt10086() {
        String businessId = "10086";
        sendedCntHandler.updateCitySendCnt(CHANNEL_ID_10086, businessId, CHANNEL_SENDED_CNT_10086_PREFIX + DateUtil.getCurrentMonthYYYYMMString());
    }

    //10086 优惠提醒 同上所述
    @Scheduled(cron = "0 0 0/1 * * ? ")
    void updateCitySendCntD01() {
        String businessId = "10658211";
        sendedCntHandler.updateCitySendCnt(CHANNEL_ID_YHTX, businessId, CHANNEL_SENDED_CNT_YHTX_PREFIX + DateUtil.getCurrentMonthYYYYMMDDStringByMinutes(-30));
    }

}
