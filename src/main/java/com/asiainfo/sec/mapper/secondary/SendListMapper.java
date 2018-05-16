package com.asiainfo.sec.mapper.secondary;

import com.asiainfo.sec.entity.PhoneNum;

import java.util.List;

/**
 * Created by Meiah on 2018/1/31.
 */
public interface SendListMapper {
    void sendList(List<PhoneNum> list);
}
