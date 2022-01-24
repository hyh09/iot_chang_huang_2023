package org.thingsboard.server.dao.util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @program: springboot-jpa-20210106
 * @description: 字符串工具类
 * @author: HU.YUNHUI
 * @create: 2021-11-08 13:26
 **/

public class StringUtilToll {

    private final static String zero ="0";


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

    /**
     * 减法
     * @param value1
     * @param value2
     * @return
     */
    public static String  sub(String value1, String value2){
        if(value1 == null  ||  value2 == null)
        {
            return zero;
        }
        BigDecimal b1 = new BigDecimal(value1);
        BigDecimal b2 = new BigDecimal(value2);
        BigDecimal  result = b1.subtract(b2).stripTrailingZeros();
        return  result.compareTo(BigDecimal.ZERO )<0?"0":roundUp(result.toPlainString());
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




    private static final int DEF_DIV_SCALE = 10;

    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
     * 小数点以后10位，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static String div(String v1,String v2,String v3){
        if(v1 == null || v2 == null ||  v3 == null)
        {
            return  zero;

        }
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        BigDecimal b3 = new BigDecimal(v3);
        if(b2.compareTo(BigDecimal.ZERO)==0)
        {
            return zero;
        }
        if(b3.compareTo(BigDecimal.ZERO)==0)
        {
            return zero;
        }
        BigDecimal bigDecimal =  b1.divide(b2, 2, BigDecimal.ROUND_HALF_UP).divide(b3,2, BigDecimal.ROUND_HALF_UP);
        //保留4位小数
        String str=  bigDecimal.stripTrailingZeros().toPlainString();
       return str;
    }


    public static String div(String v1,String v2){
        if(v1 == null || v2 == null)
        {
            return  zero;

        }
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        if(b2.compareTo(BigDecimal.ZERO)==0)
        {
            return zero;
        }
        BigDecimal bigDecimal =  b1.divide(b2, 2, BigDecimal.ROUND_HALF_UP);
        String str=  bigDecimal.stripTrailingZeros().toPlainString();
        return str;
    }



      /*** 精确乘法　*/
      public static String mul(String v1, String v2) {
          String zero ="0";
          if(v2 == null || v2 == null )
          {
              return  zero;

          }
          BigDecimal b1 = new BigDecimal(v1);
          BigDecimal b2 = new BigDecimal(v2);
          BigDecimal bigDecimal=  b1.multiply(b2);
          String str=  bigDecimal.stripTrailingZeros().toPlainString();
         return  roundUp(str);
      }


    /**
     * 保留4位小数
     */
    public  static  String roundUp(String num)
    {
         if(num == null)
         {
             return  "0";
         }
        BigDecimal b = new BigDecimal(num);
        //保留4位小数
        BigDecimal result = b.setScale(2, BigDecimal.ROUND_HALF_UP);
        if (result.compareTo(BigDecimal.ZERO) == -1){
            return  "0";
        }
        String str=  result.stripTrailingZeros().toPlainString();
        return  str;
    }


    public  static  Boolean isZero(String v2)
    {
        BigDecimal b2 = new BigDecimal(v2);
        if(b2.compareTo(BigDecimal.ZERO)==0)
        {
            return true;
        }
        return  false;

    }

    /**
     * 将时间戳转换天
     * @param timestamp
     * @return
     */
    public  static LocalDate getLocalDateByTimestamp(long timestamp)
    {
        LocalDate localDate = Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.ofHours(8)).toLocalDate();
        return  localDate;
    }


    /**
     * 累加器
     * @param finalValueList  number类型
     * @return
     */
    public  static  String  accumulator(List<String> finalValueList)
    {
        BigDecimal value =   finalValueList.stream()
                .filter(m1-> StringUtils.isNotEmpty(m1))
                .map(BigDecimal::new).reduce(BigDecimal.ZERO,
                        BigDecimal::add);
        String value03= roundUp(value.stripTrailingZeros().toPlainString());
        return  value03;
    }








}
