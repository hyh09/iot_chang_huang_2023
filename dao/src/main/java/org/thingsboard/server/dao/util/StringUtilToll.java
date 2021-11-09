package org.thingsboard.server.dao.util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * @program: springboot-jpa-20210106
 * @description: 字符串工具类
 * @author: HU.YUNHUI
 * @create: 2021-11-08 13:26
 **/

public class StringUtilToll {

    /**
     * 判断一个字符串是否是数字。
     *
     * @param string
     * @return
     */
    public static boolean isNumber(String string) {
        if (StringUtils.isEmpty(string))
            return false;
        Pattern pattern = Pattern.compile("^-?\\d+(\\.\\d+)?$");
        return pattern.matcher(string).matches();
    }


    public static double sub(Double value1, Double value2){
        BigDecimal b1 = new BigDecimal(value1.toString());
        BigDecimal b2 = new BigDecimal(value2.toString());
        return b1.subtract(b2).doubleValue();
    }


    /**
     * 加法
     * @param value1
     * @param value2
     * @return
     */
    public static double add(String value1,String value2){
        BigDecimal b1 = new BigDecimal(value1);
        BigDecimal b2 = new BigDecimal(value2);
        return b1.add(b2).doubleValue();
    }





}
