package com.asiainfo.sec.util;

import com.asiainfo.sec.entity.SmsBean;
import com.asiainfo.sec.model.ConstantPojo;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 过滤白名单用户
 * Created by PuMg on 2017/10/31/0031.
 */
@Component
public class FilterWhiteListUtil {

    private static List<String> WHITE_LIST = ConstantPojo.getWhiteList();

    /**
     * 不含自定义营销用语
     * @param userList
     * @return Map key -> userList:目标用户信息（SmsBean）whiteList:白名单用户信息（SmsBean）
     */
   // @Async
    public Map<String,List<String>> filterWhiteListAll(List<String> userList) {
        Map resultMap = new HashMap<>();
        List<String> diff = new ArrayList<>();
        List<String> same = new ArrayList<>();

        Map<String,Integer> whiteListMap  = WHITE_LIST.stream().collect(Collectors.toMap(s -> s,s1 -> 1));

        for (String string : userList) {
           if(whiteListMap.containsKey(string)){
               same.add(string);
           }else{
               diff.add(string);
           }
        }
        resultMap.put("userList",diff);
        resultMap.put("whiteList",same);

        return resultMap;
    }

    /**
     * 带自定义营销用语
     * @param userList
     * @param whiteList
     * @return Map key -> userList:目标用户信息（SmsBean）whiteList:白名单用户信息（SmsBean）
     */
    @Async
    public Map filterWhiteListSingle(List<SmsBean> userList, List<String> whiteList) {
        Map resultMap = new HashMap<>();
        Map<String,Integer> map = new HashMap<String,Integer>(userList.size()+whiteList.size());
        List<SmsBean> resultUserList = new ArrayList<>();
        List<SmsBean> sameWhiteList = new ArrayList<>();

        //保存原始目标用户信息
        Map<String, String> allMap = userList.stream().collect( Collectors.toMap(SmsBean::getPhoneNo, (s) -> s.getSendMsg()));

        List<String> maxList = userList.stream().map(smsBean1 -> smsBean1.getPhoneNo()).collect(Collectors.toList());

        List<String> minList = whiteList;
        if(maxList.size()<minList.size())
        {
            maxList = minList;
            minList = maxList;
        }
        maxList.stream().forEach(s -> {
            map.put(s,1);
        });

        for (String string : minList) {
            Integer cc = map.get(string);
            if(cc!=null)
            {
                map.put(string, ++cc);
                continue;
            }
            map.put(string, 1);
        }
        for(Map.Entry<String, Integer> entry:map.entrySet()){
            SmsBean tmpBean = new SmsBean();
            if(entry.getValue()==1)
            {   tmpBean.setPhoneNo(entry.getKey());
                tmpBean.setSendMsg(allMap.get(entry.getKey()));
                resultUserList.add(tmpBean);
            }else{
                tmpBean.setPhoneNo(entry.getKey());
                tmpBean.setSendMsg(allMap.get(entry.getKey()));
                sameWhiteList.add(tmpBean);
            }
        }
        resultMap.put("userList",resultUserList);
        resultMap.put("whiteList",sameWhiteList);

        return resultMap;
    }

    public static void main(String[] args) {
//        List<SmsBean> users = new ArrayList<>();
//        List<String> whitte = new ArrayList<>();
//        for (int i =0 ;i<5000000;i++){
//            SmsBean tmpBean = new SmsBean();
//            tmpBean.setPhoneNo(String.valueOf(i));
//            String msg = "";
//            if(i%2 == 0 ){
//                whitte.add(String.valueOf(i));
//                msg = "sms content is "+i;
//            }
//            tmpBean.setSendMsg(msg);
//
//            users.add(tmpBean);
//        }
//
//        FilterWhiteListUtil test = new FilterWhiteListUtil();
      // Map map = test.filterWhiteList(users,whitte);
        //System.out.println("users: {}"+map.get("userList").toString());
        //System.out.println("white: {}"+map.get("whiteList").toString());

        List<String> listStrs = new ArrayList<>();
        for(int i=0;i<132;i++){
            listStrs.add(String.valueOf(i));
        }

        for(int j=0;j<listStrs.size();j+=20){
            System.out.println("j = "+j);
            int size = j+20 >listStrs.size() ? j+(listStrs.size()-j) : j+20;
            System.out.println("size = "+size);
        System.out.println("test:"+listStrs.subList(j,size));
        }
    }
}
