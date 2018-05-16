package com.asiainfo.sec.service.impl;

import com.asiainfo.sec.entity.PhoneNum;
import com.asiainfo.sec.mapper.primary.FilterSuringMapper;
import com.asiainfo.sec.mapper.secondary.SendListMapper;
import com.asiainfo.sec.service.FilterSuringService;
import com.asiainfo.sec.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Meiah on 2018/1/30.
 */
@Service
public class FilterSuringServiceImpl implements FilterSuringService {

    @Autowired
    private FilterSuringMapper filterSuringMapper;

    @Autowired
    private SendListMapper sendListMapper;

    @Override
    public List<PhoneNum> getPhoneNum(String tabName) {
        String yyyymmdd = DateUtil.dateRange(DateUtil.getCurrentMonthYYYYMMDDString(), -7);
        List<PhoneNum> list = filterSuringMapper.getPhoneNum(tabName, yyyymmdd);
        return list;
    }

    @Transactional(value = "secondaryTransactionManager", readOnly = false, rollbackFor = Exception.class)
    public void sendList(List<PhoneNum> list) {
        sendListMapper.sendList(list);
    }

}
