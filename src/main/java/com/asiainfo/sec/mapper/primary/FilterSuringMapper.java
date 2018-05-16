package com.asiainfo.sec.mapper.primary;

import com.asiainfo.sec.entity.PhoneNum;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Meiah on 2018/1/30.
 */
public interface FilterSuringMapper {
    List<PhoneNum> getPhoneNum(@Param("tabName") String tabName, @Param("yyyymmdd") String yyyymmdd);
}
