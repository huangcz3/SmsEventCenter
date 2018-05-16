package com.asiainfo.sec.mapper.primary;

import com.asiainfo.sec.entity.ActiveSmsBean;
import com.asiainfo.sec.entity.CntBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by PuMg on 2017/11/1/0001.
 */
public interface ActiveScanMapper {

    /**
     * 获取需要发送短信的活动列表
     */
    List<ActiveSmsBean> getActSmsSendList(Map map);

    /**
     * 获取各地市配额
     * @param channelId
     * @return
     */
    List<CntBean> getQuotaNum(@Param("channelId") String channelId, @Param("currentyyyymm") String currentyyyymm,@Param("appointyyyymm") String appointyyyymm);

    /**
     * 获取数据库当前时间
     * @return
     */
    String getDB2CurrentDate();

    /**
     * 保存短信活动发送信息记录
     * @param activeSmsBean
     */
    void saveSmsActivitySendInfo(ActiveSmsBean activeSmsBean);

    void updateSmsActivitySendInfo(Map parameterMap);
}
