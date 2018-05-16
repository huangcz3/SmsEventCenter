package com.asiainfo.sec.service;

import com.asiainfo.sec.entity.PhoneNum;

import java.util.List;

/**
 * Created by Meiah on 2018/1/30.
 */
public interface FilterSuringService {
    List<PhoneNum> getPhoneNum(String tabName);
    void sendList(List<PhoneNum> list);
}
