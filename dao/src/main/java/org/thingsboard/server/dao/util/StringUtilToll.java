package org.thingsboard.server.dao.util;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class StringUtilToll {

    private final static String zero ="0";
    private static final Pattern PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");


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
        //2022-07-06 柬埔寨生产遇到产量的上传一个  负的上亿数据,
        if(b2.compareTo(BigDecimal.ZERO)<0)
        {
            return zero;
        }
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
     * 保留2位小数
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
            if(!isNumeric(v2))
                {
                    return false;
                }
            if(v2 == null)
                {
                    return  false;
                }
            BigDecimal b2 = new BigDecimal(v2);
            if(b2.compareTo(BigDecimal.ZERO)==0)
                {
                    return true;
                }
            return  false;

    }




    /**
     * 是否是 0  用于运行状态的返回
     * @param v2
     * @return
     */
    public  static  Boolean isNotZero(String v2)
    {
        if(isNumber(v2)) {
            BigDecimal b2 = new BigDecimal(v2);
            if (b2.compareTo(BigDecimal.ZERO) == 0) {
                return false;
            }
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

    /**
     *str < str
     * @return 判断 Stri是否大于 str2
     */
    public  static  Boolean compareTo(String str1,String str2){
        String zero ="0";
        if(StringUtils.isEmpty(str1)  )
        {
            str1= zero;
        }
        if(StringUtils.isEmpty(str2)  )
        {
            str2= zero;
        }
        BigDecimal b1 = new BigDecimal(str1);
        BigDecimal b2 = new BigDecimal(str2);
       int flg= b1.compareTo(b2);
       if(flg>0)
       {
           return  true;
       }

       return  false;


    }



    public  static  Long  getMaxByLong(List<Long> finalValueList)
    {

        Long maxValue=   finalValueList.stream().filter(f -> f != null).max((x,y)->{
            if(compareTo(x.toString(),y.toString()))
                return 1;
            else
                return -1;
        }).orElse(0L);
        return  maxValue;

    }


    public  static  String  getMaxSum(List<String> finalValueList)
    {

        String maxValue=   finalValueList.stream().filter(s->StringUtils.isNotEmpty(s)).max((x,y)->{
            if(compareTo(x,y))
                return 1;
            else
                return -1;
        }).orElse("0");
        return  maxValue;

    }








        public static boolean isNumeric(String strNum) {
                if (strNum == null)
                {
                    return false;
                }
            return PATTERN.matcher(strNum).matches();
        }




}
