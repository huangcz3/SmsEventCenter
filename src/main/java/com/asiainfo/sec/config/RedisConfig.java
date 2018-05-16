package com.asiainfo.sec.config;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 初始化redis配置类
 */
@Configuration
public class RedisConfig {
    @Value("${spring.redis.cluster.nodes}")
    private String clusterNodes;
    @Value("${spring.redis.cluster.password}")
    private String passWord;
    private int connectionTimeout = 2000;
    private int soTimeout = 3000;
    private int maxRedirections = 5;

    @Bean
    public JedisCluster getJedisCluster() {
        Set<HostAndPort> jedisClusterNodes = new HashSet<>();
        for (String node : Splitter.on(",").split(clusterNodes)) {
            List<String> nodes = Splitter.on(":").splitToList(node);
            jedisClusterNodes.add(new HostAndPort(nodes.get(0), Integer.valueOf(nodes.get(1))));
        }
        if (Strings.isNullOrEmpty(passWord)){
            return new JedisCluster(jedisClusterNodes);
        }
        return new JedisCluster(jedisClusterNodes, connectionTimeout, soTimeout, maxRedirections, passWord, new GenericObjectPoolConfig());
    }

}
