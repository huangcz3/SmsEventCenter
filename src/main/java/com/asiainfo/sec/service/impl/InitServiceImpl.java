package com.asiainfo.sec.service.impl;

import com.asiainfo.sec.mapper.primary.InitMapper;
import com.asiainfo.sec.service.InitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PuMg on 2017/11/2/0002.
 */
@Service
public class InitServiceImpl implements InitService {

    @Autowired
    private InitMapper initMapper;

    @Override
    public Map getChannelQuota() {
       Map map =  new HashMap<>() ;
        map.put("date",initMapper.getChannelQuota());
        return map;
    }
}
