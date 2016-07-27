package com.pfms.util;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Fred on 2015/12/7.
 */
public class PersonalUtil {

    public static String intToString(int number, int index) {
        String tmp = String.valueOf(number);
        if (tmp.getBytes().length >= index) {
            return tmp;
        } else {
            for (int i = tmp.getBytes().length; i < index; i++) {
                tmp = "0" + tmp;
            }
            return tmp;
        }
    }

    public static String bigDecimalToString(BigDecimal bigDecimal) {
        //金额数字转换成String类型数据
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format(bigDecimal);
    }

    //根据给定的str范例，获取对应的日期显示
    public static String getDateStr(String regExp){
        SimpleDateFormat sdf = new SimpleDateFormat(regExp);
        return sdf.format(new Date());
    }

    public static Date getStrToDate(String date, String regExp) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(regExp);
        return sdf.parse(date);
    }

    //根据给定的str范例，获取对应的日期显示
    public static String getDateStr(String regExp, Date date){
        SimpleDateFormat sdf = new SimpleDateFormat(regExp);
        return sdf.format(date);
    }

    public static Date getDateOfMonth(int year, int month, String type) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        if (Constants.DAY_OF_MONTH_BEGIN.equals(type)) {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar.getTime();
        } else {
            int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            calendar.set(Calendar.DAY_OF_MONTH, lastDay);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            return calendar.getTime();
        }
    }
}
