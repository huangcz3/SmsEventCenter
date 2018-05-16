package com.asiainfo.sec.handler;

import com.asiainfo.sec.entity.ActiveSmsBean;
import com.asiainfo.sec.mapper.secondary.SendSmsMapper;
import com.asiainfo.sec.model.ConstantPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.List;

/** 超额活动处理类
 * Created by PuMg on 2017/12/29/0029.
 */
@Component
public class ExtreaHandler {

    private static final Logger logger = LoggerFactory.getLogger(ExtreaHandler.class);

    @Autowired
    private JedisCluster jedisCluster;

    @Autowired
    private SendSmsMapper sendSmsMapper;

    @Async
    public void extraUsersHandler(ActiveSmsBean activeSmsBean){

        String activityId = activeSmsBean.getActivityId();

        logger.info("开始处理活动：{}，超额目标用户信息 . . . ",activityId);

        String tabName = ConstantPojo.getExtraUsersTableName() + activityId;
        sendSmsMapper.createExtraTab(tabName);

        String cursor = "0";
        ScanResult<String> scan;
        ScanParams scanParams = new ScanParams();
        scanParams.count( ConstantPojo.BATCH_INSERT_MAX_ROWS);
        do {
            scan = jedisCluster.sscan(ConstantPojo.REDIS_KEY_USER_LIST + activeSmsBean.getActivityId(), cursor,scanParams);
            List<String> result = scan.getResult();
            cursor = scan.getStringCursor();

            sendSmsMapper.recordExtraUsers(result,tabName);

        } while (!cursor.equals("0") );
        logger.info("开始处理活动：{}，超额目标用户信息处理结束 ！ ",activityId);
    }


    @Async
    public void extraUserList(String activityId,List<String> list){

        logger.info("redis 超额用户入库开始用户量：",list.size());
        String tabName = ConstantPojo.getExtraUsersTableName() + activityId;
        sendSmsMapper.createExtraTab(tabName);

        int maxRows = ConstantPojo.BATCH_INSERT_MAX_ROWS;
        for(int i=0;i<list.size();i+=maxRows){
            int size = i+maxRows > list.size() ? list.size() : i+maxRows;
            List<String> tmpList = list.subList(i,size);
            sendSmsMapper.recordExtraUsers(tmpList,tabName);
        }
        logger.info("redis 超额用户入库结束！");
    }
}
