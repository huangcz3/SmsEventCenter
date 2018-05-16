package com.asiainfo.sec.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;

import java.util.HashMap;
import java.util.Map;

/**
 * 更新地市发送量
 * Created by PuMg on 2017/11/2/0002.
 */
@Component
public class UpdateSendCntHandler {

    private static final Logger logger = LoggerFactory.getLogger(UpdateSendCntHandler.class);

    @Autowired
    private JedisCluster jedisCluster;

    @Autowired
    SendedCntHandler sendedCntHandler;


    /**
     * 更新地市发送量
     *
     * @param quotaKey   配额key
     * @param sendedKey  渠道已发送量key
     * @param cityId     地市id
     * @param activityId 活动id
     * @param userSize   上传用户群大小
     * @param channelId  渠道id
     * @return sendCnt   可发送用户数
     */
    public Long updateSendcnt(String quotaKey, String sendedKey, String channelId, String cityId, String activityId, long userSize, int type) {
        synchronized (this) {
            String city = cityId.trim();
            //获取当前地市发送量
            Map<String, String> sendMap = jedisCluster.hgetAll(sendedKey);

            // 为空则赋值
            if (sendMap == null || sendMap != null && sendMap.size() < 1) {
                sendMap = getSendMap();
            }
            Long allSendCnt = Long.parseLong(sendMap.get(city).trim());
            logger.info("渠道===> {},地市===> {},发送量更新前===> {}", channelId, cityId, sendMap.toString());

            Long sendCnt = Long.valueOf(0);
            if (type == 1) {
                //获取地市配额
                Map<String, String> quotaMap = jedisCluster.hgetAll(quotaKey);
                // 为空则赋值
                if (quotaMap == null || quotaMap != null && quotaMap.size() < 1) {
                    quotaMap = sendedCntHandler.updateQuatoByMonthDay(-1, quotaKey, channelId, 2);
                }
                Long cityQuota = Long.parseLong(quotaMap.get(city).toString());
                logger.info("渠道===> {},地市-> {},发送配额-> {}", channelId, cityId, cityQuota.toString());

                //获取剩余可发送量，判断用户是否超额
                Long rest = cityQuota - allSendCnt;
                if (userSize > rest) {
                    sendCnt = rest;
                    logger.info("渠道===> {},归属地市===> {},活动===> {},用户已超额===> {}", channelId, cityId, activityId, userSize - rest);
                } else {
                    sendCnt = userSize;
                }

                //更新redis中当前地市短信发送量
                sendMap.put(city, String.valueOf(allSendCnt + sendCnt));
                jedisCluster.hmset(sendedKey, sendMap);
                logger.info("渠道===> {},地市===> {},更新后剩余可发送量===> {},已发送量===> {}", channelId, cityId, cityQuota - (allSendCnt + sendCnt), sendMap.get(city).toString());

                return sendCnt;
            } else { //白名单用户不占用地市配额
                sendMap.put(city, String.valueOf(allSendCnt - userSize));
                jedisCluster.hmset(sendedKey, sendMap);
                return sendCnt;
            }

        }

    }


    /**
     * 初始化 22 个地市发送量为 0
     *
     * @return
     */
    public Map<String, String> getSendMap() {
        Map<String, String> map = new HashMap<>();
        for (int i = 1; i <= 22; i++) {
            map.put(i + "", "0");
        }
        return map;
    }
}
