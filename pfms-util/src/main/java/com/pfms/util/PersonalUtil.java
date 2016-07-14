package com.pfms.util;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Fred on 2015/12/7.
 */
public class PersonalUtil {

    public String intToString(int number, int index) {
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
}
