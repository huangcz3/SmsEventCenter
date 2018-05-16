package com.asiainfo.sec.service;


import com.asiainfo.sec.entity.ActiveSmsBean;
import com.asiainfo.sec.entity.CntBean;

import java.util.List;
import java.util.Map;

/**
 * Created by lujango on 2017/9/25.
 */
public interface SMSCenterService {

    /**
     * 获取需要通过短息中心的发送的活动信息
     * @return
     */
    List<ActiveSmsBean> getActiveList(String channelId);


    /**
     * 判断活动是否存在
     */
    int isExsist(String activityId);


    /**
     * 统计 d01(10086)、d05(优惠提醒)渠道 地市配额
     * @param channelId
     * @return
     */
    List<CntBean> getQuotaNum(String channelId,String yyyymm);

    /**
     * 统计一段时间内短息发送失败数
     */
    List<CntBean> getSendFailedNum(String businessId);

    /**
     * 保存短信活动发送信息记录
     * @param activeSmsBean
     */
    void saveSmsActivitySendInfo(ActiveSmsBean activeSmsBean);

    /**
     * 更新短信活动发送信息记录的记录量
     * @param parameterMap
     */
    void updateSmsActivitySendInfo(Map parameterMap);
}
