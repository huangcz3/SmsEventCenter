package com.asiainfo.sec.entity;

/**
 * Created by PuMg on 2017/9/27/0027.
 * 短息信息
 */
public class SmsBean {

    private String phoneNo;

    private String sendMsg;


    public String getSendMsg() {
        return sendMsg;
    }

    public void setSendMsg(String sendMsg) {
        this.sendMsg = sendMsg;
    }
    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    @Override
    public String toString() {
        return "SmsBean{" +
                "phoneNo='" + phoneNo + '\'' +
                ", sendMsg='" + sendMsg + '\'' +
                '}';
    }


}
