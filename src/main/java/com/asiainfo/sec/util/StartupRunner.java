package com.asiainfo.sec.util;

import com.asiainfo.sec.model.ConstantPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;

import java.util.Set;

/**
 * 程序启动初始化类
 * Created by PuMg on 2017/10/25/0025.
 */
@Component
@Order(value = 2)
public class StartupRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(StartupRunner.class);

    @Autowired
    private JedisCluster jedisCluster;

    @Override
    public void run(String... args) throws Exception {

        //白名单查询初始化
        Set<String> whiteSet = jedisCluster.smembers("SET-SEND-SMS-WHITE-LIST");
        ConstantPojo.WHITE_LIST.addAll(whiteSet);
        logger.info("初始化白名单成功，白名单用户数：{}",whiteSet.size());

    }
}
