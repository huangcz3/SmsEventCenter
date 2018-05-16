package com.asiainfo.sec.task;

import com.asiainfo.sec.entity.PhoneNum;
import com.asiainfo.sec.model.ConstantPojo;
import com.asiainfo.sec.service.FilterSuringService;
import com.asiainfo.sec.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Meiah on 2018/1/30.
 */
//过滤订购了冲浪的用户
@Component
public class FilterSurfingTask {

    private Logger logger = LoggerFactory.getLogger(FilterSurfingTask.class);

    @Autowired
    private FilterSuringService filterSuringService;

    private int MAX_ROWS = ConstantPojo.YHTX_BATCH_INSERT_MAX_ROWS;

    private int taskNo = 0;

    @Scheduled(cron = "0 0 9 ? * WED")
    void filterSuringTask() {
        logger.info("和我看短信推送任务启动序号：{}",++taskNo);
        String nowDate = DateUtil.getCurrentMonthYYYYMMDDString();
        String queryDate = DateUtil.dateRange(nowDate, -4);
        String tabName = "aiapp.dwd_data_label_coc_" + queryDate;

        List<PhoneNum> phoneList = filterSuringService.getPhoneNum(tabName);
        if (phoneList.size() > 0) {
            int i = 0;
            do {
                int len = (i + 1) * MAX_ROWS < phoneList.size() ? (i+1) * MAX_ROWS : phoneList.size();
                filterSuringService.sendList(phoneList.subList(i * MAX_ROWS,len));
                i++;
            }
            while (i * MAX_ROWS < phoneList.size());
            logger.info("和我看短信推送结束，目标用户数： {}",phoneList.size());
        }
    }
}
