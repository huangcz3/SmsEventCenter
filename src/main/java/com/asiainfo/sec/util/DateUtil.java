package com.asiainfo.sec.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyyMM");
    public static final DateTimeFormatter monthFormatter2 = DateTimeFormatter.ofPattern("yyyy-MM");
    public static final DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static String getCurrentMonthYYYYMMString() {
        return LocalDate.now().format(monthFormatter);
    }
    public static String getCurrentMonthYYYYMMStringByHours(int hours) {
        return LocalDateTime.now().plusHours(hours).format(monthFormatter2);
    }

    public static String getCurrentMonthYYYYMMDDString() {
        return LocalDate.now().format(dayFormatter);
    }

    public static String getCurrentMonthYYYYMMDDStringByMinutes(int minutes) {
        return LocalDateTime.now().plusMinutes(minutes).format(dayFormatter);
    }


    public static String getNextMonthYYYYMMString() {
        return LocalDate.now().plusMonths(1).format(monthFormatter);
    }

    public static String getTimeYYYYMMDDHHDDSS(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new Date());
    }

    /**
     * 当前时间加上小时
     * @param hour
     * @return
     */
    public static String plusTime(int hour){
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR,hour);//当前时间，加上hour后的时间
        String str = dateToStr(calendar.getTime());
        return str;
    }

    /**
     * 字符串转换成日期
     * @param str
     * @return date
     */
    public static Date strToDate(String str) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    /**
     * 日期转换成字符串
     * @param date
     * @return str
     */
    public static String dateToStr(Date date) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = format.format(date);
        return str;
    }

    /**
     * 指定时间加或减几天的时间
     * @param dateStr
     * @param num
     * @return
     */
    public static String dateRange(String dateStr,int num){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR,num);
        return format.format(cal.getTime());
    }

    /**
     *
     * @param endTime
     * @param startTime
     * @return
     */
    public static int getDateSpace(String endTime, String startTime) {
        //算两个日期间隔多少天
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date date1 = null;
        try {
            date1 = format.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date2 = null;
        try {
            date2 = format.parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int a = (int) ((date1.getTime() - date2.getTime()) / (1000*3600*24));
        return a;
    }

    /**
     * 获取指定日期所在月有多少天
     * @param date
     * @return
     */
    public static int getDaysOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }



    public static void main(String[] args){
        int i = getDateSpace("20170111","20170110");
        System.out.println(i);
    }


}
