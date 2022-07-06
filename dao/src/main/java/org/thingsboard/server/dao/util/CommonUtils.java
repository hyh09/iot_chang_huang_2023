package org.thingsboard.server.dao.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.thingsboard.server.common.data.vo.home.EachMonthStartEndVo;

import java.beans.PropertyDescriptor;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

/**
 * 通用工具
 */
public class CommonUtils {
	
	/**
	 * 获取导致Exception异常的真正原因信息
	 * @return
	 */
	public static String getExceptionCauseMessage(Throwable e) {
		if(e.getCause() != null) {
			return CommonUtils.getExceptionCauseMessage(e.getCause());
		} else {
			return e.getMessage();
		}
	}

    /**
     * 将Exception中的堆栈信息转换成字符串
     * @param e
     * @return
     */
    public static String getExceptionStackTraceToString(Throwable e){
    	StringBuffer strBuf = new StringBuffer();
    	if(e != null){
	    	StackTraceElement[] stList = e.getStackTrace();
	    	StackTraceElement st = null;
	    	for(int i=0; i<stList.length; i++){
	    		st = stList[i];
	    		if(i==0){
	    			strBuf.append(e.getMessage()).append("\t").append(st.getClassName()).append(".").append(st.getMethodName())
	    				.append("(").append(st.getFileName()).append(":").append(st.getLineNumber()).append(")");
	    		}else{
	    			strBuf.append("\tat  ")
	    				.append(st.getClassName()).append(".").append(st.getMethodName())
	    				.append("(").append(st.getFileName()).append(":").append(st.getLineNumber()).append(")");
	    		}
	    	}
    	}
    	return strBuf.toString();
    }
    
    /**
     * 取得当前应用服务器时间
     * @return
     */
    public static Timestamp getCurentAppServerTimestamp(){
    	return new Timestamp(System.currentTimeMillis());
    }


    /**
     * 获得当前日期 yyyy-MM-dd HH:mm:ss
     *
     * @return 2019-08-27 14:12:40
     */
    public static String getCurrentTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return df.format(date);
    }


    
    /**
     * 取得当前应用服务器时间(date)
     */
    public static Date getCurentAppServerDate(){
    	return new Date(getCurentAppServerTimestamp().getTime());
    }
    
    /**
     * 把得到的完整类名，首字母变小写
     * @param className
     * @return
     */
    public static String lowerFirstBeanName(String className){
    	
    	 String tempstring = className.substring(className.lastIndexOf(".")+1,className.lastIndexOf(".")+2);
         className =className.substring(className.lastIndexOf(".")+2,className.length());
         className =tempstring.toLowerCase()+className;
         return className;
    }
    
    /**
     * 将空值的属性从目标实体类中复制到源实体类中
     * @param src : 要将属性中的空值覆盖的对象(源实体类)
     * @param target :从数据库根据id查询出来的目标对象
     */
    public static void copyNonNullProperties(Object src, Object target){
        BeanUtils.copyProperties(src,target,getNullProperties(src));
    }

    /**
     * 将为空的properties给找出来,然后返回出来
     * @param src
     * @return
     */
    public static String[] getNullProperties(Object src){
        BeanWrapper srcBean=new BeanWrapperImpl(src);
        PropertyDescriptor[] pds=srcBean.getPropertyDescriptors();
        Set<String> emptyName=new HashSet<>();
        for(PropertyDescriptor p:pds){
            Object srcValue=srcBean.getPropertyValue(p.getName());
            if(srcValue==null) emptyName.add(p.getName());
        }
        String[] result=new String[emptyName.size()];
        return emptyName.toArray(result);
    }


    /**
     * 获取当天的零时时间
     */
    public  static  long  getZero()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date zero = calendar.getTime();
      return   zero.getTime();
    }

    /**
     * 获取当前的时间
     */
    public  static  long getNowTime()
    {
        Date  date =  new Date();
        return  date.getTime();
    }


    /**
     * 获取昨天零点
     * @return
     */
    public  static  long  getYesterdayZero()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date zero = calendar.getTime();
        return   zero.getTime();
    }

    /***
     * 获取昨天最后一刻时间
     * @return
     */
    public  static  long  getYesterdayLastTime()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date zero = calendar.getTime();
        return   zero.getTime();
    }


    /**
     * 获取1949年的时间
     */
    public  static  long getHistoryPointTime()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(1949, 10, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date zero = calendar.getTime();
       return  zero.getTime();
    }

    static SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取当月的前5月
     * 包含当月
     * @return
     */
    public static List<EachMonthStartEndVo> getSixMonths()
    {
        List<EachMonthStartEndVo> voList  = new ArrayList<>();
        for(int i=-5;i<1;i++) {
            voList.add(getEachMonthStartEndTime(i));
        }
       return  voList;
    }




    public static  EachMonthStartEndVo getEachMonthStartEndTime(int amount)
    {

        EachMonthStartEndVo vo = new EachMonthStartEndVo();
        vo.setFlg(amount);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, amount);
        c.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        //将小时至0
        c.set(Calendar.HOUR_OF_DAY, 0);
        //将分钟至0
        c.set(Calendar.MINUTE, 0);
        //将秒至0
        c.set(Calendar.SECOND,0);
        //将毫秒至0
        c.set(Calendar.MILLISECOND, 0);
        // 获取本月第一天的时间戳
        Date zero = c.getTime();
        String s = format1.format(zero);
        vo.setStartTime(zero.getTime());
        vo.setStrStartTime(s);

        //获取当前月最后一天
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.MONTH, amount);
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        //将小时至0
        ca.set(Calendar.HOUR_OF_DAY, 23);
        //将分钟至0
        ca.set(Calendar.MINUTE, 59);
        //将秒至0
        ca.set(Calendar.SECOND,59);
        //将毫秒至0
        ca.set(Calendar.MILLISECOND, 59);
        // 获取本月最后一天的时间戳
        Date zero2 = ca.getTime();
        String s2 = format1.format(zero2);
        vo.setEndTime(zero2.getTime());
        vo.setStrEndTime(s2);

        vo.setMonth(ca.get(Calendar.MONTH) + 1);
        return  vo;

    }


    //判断是否是当天的时间
    //判断是否是当天的时间
    public static Boolean  isItToday(long  time)
    {
        long    todayZero =  getZero();
        long  now =getNowTime();
        if(time>=todayZero &&  time <=now)
        {
            return true;
        }

        return  false;

    }


    /**
     * 判断是不是今天的数据
     * @param localDate
     * @return
     */
    public  static   Boolean  itIsToday(LocalDate localDate)
    {
        LocalDate  nowDate = LocalDate.now();// 今天
        int i =  nowDate.compareTo(localDate);
        if(i == 0 )
        {
            return  true;
        }
        return  false;

    }

    /**
     * 时间戳转换LocalDate
     * @param timeMillis
     * @return
     */
    public static LocalDate  getLocalDateByLong(Long timeMillis)
    {
       return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeMillis), ZoneId.systemDefault()).toLocalDate();
    }


    /**
     *  获取所在时间所在时间片段
     *   每隔30分钟统计一次
     * @param ts
     * @return
     */
    public  static  Long  getTimeClip(long  ts)
    {
        LocalDateTime localDateTime1 = null;
        LocalDateTime localDateTime =longToDateTime(ts);
        int year =  localDateTime.getYear();
        Month month =  localDateTime.getMonth();
        int day =  localDateTime.getDayOfMonth();
        int hour =  localDateTime.getHour();
        int minute = localDateTime.getMinute();
        int second =localDateTime.getSecond();
        if(minute >30)
        {
            if(hour>=23){
                hour =-1;
            }
            localDateTime1  =  LocalDateTime.of(year,month,day,hour+1,0,0,0);
        }else  if(minute == 30 && second>0){
            if(hour>=23){
                hour =-1;
            }
            localDateTime1  =  LocalDateTime.of(year,month,day,hour+1,0,0,0);
        }
        else  if(minute == 0 && second== 0){
            localDateTime1  =LocalDateTime.of(year,month,day,hour,0,0,0);
        }else {
            localDateTime1  =  LocalDateTime.of(year,month,day,hour,30,0,0);

        }
        return getTimestampOfDateTime(localDateTime1);
    }


    /**
     *  Conversion minutes
     * @param ts
     * @return 转换分钟
     */
    public  static Long getConversionMinutes(long  ts)
    {
        LocalDateTime localDateTime1 = null;
        LocalDateTime localDateTime =longToDateTime(ts);
        int year =  localDateTime.getYear();
        Month month =  localDateTime.getMonth();
        int day =  localDateTime.getDayOfMonth();
        int hour =  localDateTime.getHour();
        int minute = localDateTime.getMinute();
        int second =localDateTime.getSecond();
        localDateTime1  =  LocalDateTime.of(year,month,day,hour,minute,0,0);
        return getTimestampOfDateTime(localDateTime1);
    }


    /**
     * localDateTime转long
     * @param localDateTime
     * @return
     */
    public static long getTimestampOfDateTime(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return instant.toEpochMilli();
    }


    /**
     * long 转 LocalDateTime
     * @param l
     * @return
     */
    public static LocalDateTime longToDateTime(long l){
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(l),ZoneId.systemDefault());
    }

    /**
     * long 转 LocalTime
     * @param l
     * @return
     */
    public static LocalTime longToLocalTime(long l){
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(l),ZoneId.systemDefault()).toLocalTime();
    }


    /**
     * 获取两个时间段的整点时间
     * 目前入参： 0:00:00 -> 23:59:59
     */
    public  static  List<Long> getTwoTimePeriods(long startTs ,long  endTs)
    {
        List<Long> resultTimeList = new ArrayList<>();
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            Date startTime1 = new Date(startTs);
            Date endTime1 = new Date(endTs);
            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(startTime1);
            while (startTime1.getTime() <= endTime1.getTime()) {
                startTime1 = tempStart.getTime();
                tempStart.add(Calendar.MINUTE, 30);//30分钟前的时间
                resultTimeList.add(startTime1.getTime());
            }
            return resultTimeList;


    }


    /**
     * 获取两个时间段的整点时间
     * 目前入参： 0:00:00 -> 23:59:59
     */
    public  static  List<Long> getTwoTimePeriods(long startTs ,long  endTs,int type,int value)
    {
        List<Long> resultTimeList = new ArrayList<>();
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Date startTime1 = new Date(startTs);
        Date endTime1 = new Date(endTs);
        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(startTime1);
        while (startTime1.getTime() <= endTime1.getTime()) {
//            System.out.println(format2.format(startTime1));
            startTime1 = tempStart.getTime();
            tempStart.add(type, value);
            resultTimeList.add(startTime1.getTime());
//            System.out.println("====>"+format2.format(startTime1));
        }
        return resultTimeList;


    }




    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }


    /**
     *
     * @param ts
     * @return 小时
     */
    public  static Long getConversionHours(long  ts)
    {
        LocalDateTime localDateTime1 = null;
        LocalDateTime localDateTime =longToDateTime(ts);
        int year =  localDateTime.getYear();
        Month month =  localDateTime.getMonth();
        int day =  localDateTime.getDayOfMonth();
        int hour =  localDateTime.getHour();
        int minute = localDateTime.getMinute();
        if(minute==0) {
            localDateTime1 = LocalDateTime.of(year, month, day, hour, 0, 0, 0);
        }else if(minute>0  ) {
            localDateTime1 =localDateTime.plusHours(1).toLocalDate().atTime(localDateTime.plusHours(1).getHour(),0,0);
        }
        return getTimestampOfDateTime(localDateTime1);
    }


    /**
     *
     * @param localDateTime
     * @return
     */
    public  static  Long   getZeroByLocalDateTime(LocalDateTime localDateTime)
    {
        LocalDateTime localDateTime1 = null;
        int year =  localDateTime.getYear();
        Month month =  localDateTime.getMonth();
        int day =  localDateTime.getDayOfMonth();
        int hour =  localDateTime.getHour();
        int minute = localDateTime.getMinute();
        int second =localDateTime.getSecond();
        localDateTime1  =  LocalDateTime.of(year,month,day,0,0,0,0);
        return getTimestampOfDateTime(localDateTime1);
    }






}
