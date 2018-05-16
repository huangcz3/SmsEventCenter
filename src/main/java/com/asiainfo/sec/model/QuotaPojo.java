package com.asiainfo.sec.model;

import java.util.List;
import java.util.Map;

/**
 * Created by PuMg on 2017/11/2/0002.
 */
public class QuotaPojo {

    /**
     * 渠道id
     */
    private String channelId;

    /**
     * 渠道配额
     */
    private List<Map> quotaList;


    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public List<Map> getQuotaList() {
        return quotaList;
    }

    public void setQuotaList(List<Map> quotaList) {
        this.quotaList = quotaList;
    }
}
