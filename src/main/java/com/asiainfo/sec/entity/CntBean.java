package com.asiainfo.sec.entity;

/**
 * Created by PuMg on 2017/10/12/0012.
 */
public class CntBean {

    private String ActivityId;

    private String cityId;

    private int cnt;

    public String getActivityId() {
        return ActivityId;
    }

    public void setActivityId(String activityId) {
        ActivityId = activityId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }
}
