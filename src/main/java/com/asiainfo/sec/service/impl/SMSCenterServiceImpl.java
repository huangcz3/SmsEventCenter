package com.asiainfo.sec.service.impl;


import com.asiainfo.sec.entity.ActiveSmsBean;
import com.asiainfo.sec.entity.CntBean;
import com.asiainfo.sec.mapper.primary.ActiveScanMapper;
import com.asiainfo.sec.mapper.secondary.SendSmsMapper;
import com.asiainfo.sec.model.ConstantPojo;
import com.asiainfo.sec.service.SMSCenterService;
import com.asiainfo.sec.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by lujango on 2017/9/25.
 */
@Service
public class SMSCenterServiceImpl implements SMSCenterService {

    private static final Logger logger = LoggerFactory.getLogger(SMSCenterServiceImpl.class);

    @Value("${sec.sms.init.intervalDay}")
    private int intervalDay;

    @Value("${sec.sms.init.intervalTime}")
    private int interValTime;

    @Autowired
    private ActiveScanMapper activeScanRepository;

    @Autowired
    private SendSmsMapper sendSmsRepository;

    @Autowired
    private JedisCluster jedisCluster;

    @Override
    public List<ActiveSmsBean> getActiveList(String channelId) {

        Map parameterMap = new HashMap<>();
        parameterMap.put("channelId",channelId);
        parameterMap.put("intervalDay",intervalDay);
       // List<ActiveSmsBean> actSmsSendList = activeScanRepository.getActSmsSendList(parameterMap);
      //  return actSmsSendList.stream().collect(Collectors.toList());
        return activeScanRepository.getActSmsSendList(parameterMap).stream()
                .filter(activeBean -> "N".equals(jedisCluster.get(ConstantPojo.REDIS_KEY_ACTIVEISPAUSED + activeBean.getActivityId())))
                .collect(Collectors.toList());
    }

    @Override
    public int isExsist(String activityId) {
        return sendSmsRepository.isExsist(activityId);
    }

    @Override
    public List<CntBean> getQuotaNum(String channelId,String yyyymm) {
        String currentyyyymm = DateUtil.getCurrentMonthYYYYMMStringByHours(-8);
        List<CntBean> list = activeScanRepository.getQuotaNum(channelId,currentyyyymm, yyyymm);
        return list;
    }

    @Override
    public List<CntBean> getSendFailedNum(String businessId) {
        //获取数据库当前时间
        String currentDate = sendSmsRepository.getCurrentDate();
        Date date = DateUtil.strToDate(currentDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR,-interValTime);
        //参数
        Map parameterMap = new HashMap<>();
        String tableName = "sms_platform_history_"+DateUtil.getCurrentMonthYYYYMMDDStringByMinutes(-30);

        int flag = sendSmsRepository.currentHistoryTableIsExist(tableName);
        if (flag == 0){
            logger.info("日记录表不存在");
            return Collections.EMPTY_LIST;
        }

        String fromTime = DateUtil.dateToStr(cal.getTime());
        String toTime = DateUtil.dateToStr(date);
        parameterMap.put("tableName",tableName);
        parameterMap.put("businessId",businessId);
        parameterMap.put("fromTime",fromTime);
        parameterMap.put("toTime",toTime);

        return sendSmsRepository.getSendFailedNum(parameterMap);
    }

    @Override
    public void saveSmsActivitySendInfo(ActiveSmsBean activeSmsBean) {
        activeScanRepository.saveSmsActivitySendInfo(activeSmsBean);
    }

    @Override
    public void updateSmsActivitySendInfo(Map parameterMap) {
        activeScanRepository.updateSmsActivitySendInfo(parameterMap);
    }
}
