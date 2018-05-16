package com.asiainfo.sec.handler;

import com.asiainfo.sec.entity.CntBean;
import com.asiainfo.sec.service.SMSCenterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;

import java.util.*;

/**
 * 地市成功发送量和地市配额更新处理类
 */
@Component
public class SendedCntHandler {
    private Logger logger = LoggerFactory.getLogger(SendedCntHandler.class);

    @Autowired
    private JedisCluster jedisCluster;

    @Autowired
    private SMSCenterService smsCenterService;

    /**
     * 更新地市配额
     *
     * @param channelId 渠道id
     * @param quotaKey  渠道配额key
     */
    //@Async
    public void updateQuato(String channelId, String quotaKey) {
        /*//获取当前地市配额
        Map<String, String> quotaMap = jedisCluster.hgetAll(quotaKey);
        logger.info("当前渠道：{}，更新前地市配额：-> {}", channelId, quotaMap.toString());

        //获取当月数据库地市配额
        List<CntBean> cntBeanList = smsCenterService.getQuotaNum(channelId);
        if (cntBeanList != null && cntBeanList.size() != 0) {
            cntBeanList.stream().forEach(cntBean -> {
                String cityId = cntBean.getCityId();
                int quotaCnt = cntBean.getCnt();
                quotaMap.put(cityId, String.valueOf(quotaCnt));
            });
            //更新redis中地市配额
            jedisCluster.hmset(quotaKey, quotaMap);
            logger.info("当前渠道：{}，更新后地市配额：-> {}", channelId, quotaMap.toString());
        }*/
    }

    /**
     * 更新地市发送量
     *
     * @param channelId
     * @param businessId
     * @param sendedCntKey
     */
    //@Async
    public void updateCitySendCnt(String channelId, String businessId, String sendedCntKey) {
        //获取当前地市发送量
        Map<String, String> sendMap = jedisCluster.hgetAll(sendedCntKey);
        logger.info("当前渠道：{}，更新前地市发送量：-> {}", channelId, sendMap.toString());
        //查询一小时内，各个地市发送短信失败的量
        List<CntBean> cntBeanList = smsCenterService.getSendFailedNum(businessId);
        if (cntBeanList != null && cntBeanList.size() != 0) {
            cntBeanList.stream().forEach(cntBean -> {
                String cityId = cntBean.getCityId();
                int failedCnt = cntBean.getCnt();
                int sendCnt = Integer.parseInt(sendMap.get(cityId.trim()).trim());
                int successCnt = sendCnt - failedCnt;
                sendMap.put(cityId, String.valueOf(successCnt));
            });
            //更新redis的地市发送量的值
            jedisCluster.hmset(sendedCntKey, sendMap);
            logger.info("当前渠道：{}，更新后地市发送量：-> {}", channelId, sendMap.toString());
        }
    }

    /**
     * 更新redis中配额
     *
     * @param monthDay  本月天数，type为 2 时无效
     * @param quotaKey  redis键
     * @param channelId 渠道id
     * @param type      1: 从1到monthDay更新， 2 ： 只更新 quotaKey
     */
    public Map updateQuatoByMonthDay(int monthDay, String quotaKey, String channelId, int type) {

        logger.info("当前渠道：{}，更新配额开始",channelId);
        Map<String, String> quatoMap = new HashMap<>();

        // 获取指定月数据库地市配额
        // 拼接年月字段
        String[] strings = quotaKey.split("_");
        String yyyymm = strings[strings.length-1];
        yyyymm = yyyymm.length() == 6 ? yyyymm : yyyymm.substring(0, 6);
        StringBuffer yyyymmSb = new StringBuffer(yyyymm);
        yyyymmSb.insert(4,"-");

        List<CntBean> cntBeanList = smsCenterService.getQuotaNum(channelId,yyyymmSb.toString());
        if (cntBeanList != null && cntBeanList.size() != 0) {
            cntBeanList.stream().forEach(cntBean -> {
                String cityId = cntBean.getCityId();
                int quotaCnt = cntBean.getCnt();
                quatoMap.put(cityId, String.valueOf(quotaCnt));
            });

            switch (type) {
                case 1:
                    for (int j = 1; j <= monthDay; j++) {
                        String key = quotaKey + String.format("%02d", j);
                        jedisCluster.hmset(key, quatoMap);
                    }
                    logger.info("优惠提醒配额从本月 1号 -- {}号更新成功", monthDay);
                    break;
                case 2:
                    jedisCluster.hmset(quotaKey, quatoMap);
                    logger.info("当前渠道：{}，更新配额成功，redis键为 {}, 配额为{}", channelId, quotaKey, quatoMap.toString());
            }
        }

        return quatoMap;
    }
}
