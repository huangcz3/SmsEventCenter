package com.asiainfo.sec.mapper.secondary;


import com.asiainfo.sec.entity.CntBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * All rights Reserved, Designed By asiainfo.com
 * @version V1.0
 * @Title:
 */
public interface SendSmsMapper {

    /**
     * 判断活动是否存在
     */
    int isExsist(String activityId);


    /**
     * 判断当日记录表是否存在
     */
    int currentHistoryTableIsExist(String tableName);

    /**
     * 统计短息发送失败用户数
     */
    List<CntBean> getSendFailedNum(Map map);

    /**
     * 获取数据库当前时间
     */
    String getCurrentDate();

    /**
     * 创建超额记录表
     */
    void createExtraTab(@Param("tabName") String tabName);

    /**
     * 记录超额用户群
     */
    void recordExtraUsers(@Param("list") List<String> smsBeanList, @Param("tableName") String tableName);

}
