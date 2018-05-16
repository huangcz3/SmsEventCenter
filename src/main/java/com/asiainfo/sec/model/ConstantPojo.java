package com.asiainfo.sec.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by PuMg on 2017/11/2/0002.
 */
public class ConstantPojo {

    /**
     * 短息发送明细表名+活动编号
     */
    public static final String USERS_TABLE_NAME = "sms_platform_list_";

    /**
     * 活动超额用户记录表名+活动编号
     */
    public static final String EXTRA_USERS_TABLE_NAME = "sms_platform_extra_list_";

    /**
     * redis key 活动目标用户list
     */
    public static final String REDIS_KEY_USER_LIST = "SET-USERLIST:";

    /**
     * redis key 活动是否暂停
     */
    public static final String REDIS_KEY_ACTIVEISPAUSED = "ISPAUSED:";

    /**
     *  批量插入最大条数
     */
    public static final int BATCH_INSERT_MAX_ROWS = 10000;
    /**
     *  批量插入最大条数
     */
    public static final int YHTX_BATCH_INSERT_MAX_ROWS = 5000;

    /**
     * redis 读取目标用户最大条数
     */
    public static final int READ_MAX_ROWS = 20000;

    /**
     * 白名单用户信息
     */
    public static List<String> WHITE_LIST = new ArrayList<>();

    /**
     * 渠道地市配额
     */
    public static Map qutoMap = new HashMap<>();

    public static List<String> getWhiteList() {
        return WHITE_LIST;
    }

    public static void setWhiteList(List<String> whiteList) {
        WHITE_LIST = whiteList;
    }

    public static String getUsersTableName() {
        return USERS_TABLE_NAME;
    }

    public static String getExtraUsersTableName() {
        return EXTRA_USERS_TABLE_NAME;
    }

    public static String getRedisKeyUserList() {
        return REDIS_KEY_USER_LIST;
    }

    public static String getRedisKeyActiveispaused() {
        return REDIS_KEY_ACTIVEISPAUSED;
    }

    public static int getBatchInsertMaxRows() {
        return BATCH_INSERT_MAX_ROWS;
    }

    public static Map getQutoMap() {
        return qutoMap;
    }

    public static void setQutoMap(Map qutoMap) {
        ConstantPojo.qutoMap = qutoMap;
    }
}
