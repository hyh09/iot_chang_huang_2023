package org.thingsboard.server.utils;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 时间工具类
 *
 * @author
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
    public static String YYYY = "yyyy";

    public static String YYYY_MM = "yyyy-MM";

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getDate() {
        return dateTimeNow(YYYY_MM_DD);
    }

    public static final String getTime() {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static final String dateTimeNow() {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static final String dateTimeNow(final String format) {
        return parseDateToStr(format, new Date());
    }

    public static final String dateTime(final Date date) {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static final String parseDateToStr(final String format, final Date date) {
        return new SimpleDateFormat(format).format(date);
    }

    public static final Date dateTime(final String format, final String ts) {
        try {
            return new SimpleDateFormat(format).parse(ts);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static final String datePath() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static final String dateTime() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
     * Date转LocalDate
     *
     * @param date
     */
    public static LocalDate date2LocalDate(Date date) {
        if (null == date) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * LocalDate转Date
     *
     * @param localDate
     * @return
     */
    public static Date localDate2Date(LocalDate localDate) {
        if (null == localDate) {
            return null;
        }
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }

    /**
     * 获取一个月天数
     *
     * @param date
     * @return
     */
    public static int getDaysOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当前日期所在季度的开始日期和结束日期
     * 季度一年四季， 第一季度：1月-3月， 第二季度：4月-6月， 第三季度：7月-9月， 第四季度：10月-12月
     *
     * @param isFirst true表示查询本季度开始日期  false表示查询本季度结束日期
     * @return
     */
    public static LocalDate getStartOrEndDayOfQuarter(Boolean isFirst) {
        LocalDate today = LocalDate.now();
        LocalDate resDate = LocalDate.now();
        if (today == null) {
            today = resDate;
        }
        Month month = today.getMonth();
        Month firstMonthOfQuarter = month.firstMonthOfQuarter();
        Month endMonthOfQuarter = Month.of(firstMonthOfQuarter.getValue() + 2);
        if (isFirst) {
            resDate = LocalDate.of(today.getYear(), firstMonthOfQuarter, 1);
        } else {
            resDate = LocalDate.of(today.getYear(), endMonthOfQuarter, endMonthOfQuarter.length(today.isLeapYear()));
        }
        return resDate;
    }

    /**
     * 方法描述: 时间分段
     *
     * @param startLocalDate =
     * @param endLocalDate   =
     * @param segments       = 分隔的段数
     * @return java.util.List<java.time.LocalDate>
     * @author yangChangQing
     * @date 2021/11/12 16:00
     */
    public static List<Date> segmentsDate(LocalDate startLocalDate, LocalDate endLocalDate, int segments) {
        int days = Long.valueOf((endLocalDate.toEpochDay() - startLocalDate.toEpochDay()) + 1).intValue();
        //小于segments天有几天返回几天
        if (days <= segments) {
            List<Date> collect = IntStream.range(0, days).mapToObj(e -> localDate2Date(startLocalDate.plusDays(e))).collect(Collectors.toList());
            return collect;
        }
        //大于segments天需要计算
        int remainder = days % segments;
        int step = days / segments;
        //segments 分隔的段数
        List<LocalDate> collect = IntStream.range(0, segments).mapToObj(e -> startLocalDate.plusDays(e * step)).collect(Collectors.toList());
        IntStream.range(0, remainder).forEach(e -> {
            IntStream.range(e + 1, segments).forEach(
                    ee -> {
                        LocalDate localDate = collect.get(ee);
                        collect.set(ee, localDate.plusDays(1));
                    }
            );
        });
        List<Date> result = collect.stream().map(e -> localDate2Date(e)).collect(Collectors.toList());
        //加上今天,就是segments+1个天节点，segments个时间段
        result.add(localDate2Date(LocalDate.now()));
        return result;
    }

    /**
     * 方法描述: 获取本月，本年，本季度的开始时间
     *
     * @param type =
     * @return java.time.LocalDate
     * @author yangChangQing
     * @date 2021/11/12 16:00
     */
    public static LocalDate buildStart(int type) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        Month month = now.getMonth();
        LocalDate startDate = null;
        //本月，本年，本季度
        switch (type) {
            case 1:
                startDate = LocalDate.of(year, month, 1);
                break;
            case 2:
                startDate = DateUtils.getStartOrEndDayOfQuarter(true);
                break;
            case 3:
                startDate = LocalDate.of(year, 1, 1);
                break;
        }
        return startDate;
    }

    /**
     * Date转 LocalDateTime
     *
     * @param date
     */
    public static LocalDateTime date2LocalDateTime(Date date) {
        ZoneId zoneId = ZoneId.systemDefault();
        Instant instant = date.toInstant();
        ZonedDateTime zonedDateTime = instant.atZone(zoneId);
        return zonedDateTime.toLocalDateTime();
    }

    /**
     * 获取同天的最大时间
     * @param date
     * @return
     */
    public static Date getDayMax(Date date) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.set(Calendar.HOUR_OF_DAY, 23);
        instance.set(Calendar.MINUTE, 59);
        instance.set(Calendar.SECOND, 59);
        return instance.getTime();
    }

    /**
     * 获取同天的最小时间
     * @param date
     * @return
     */
    public static Date getDayMin(Date date) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        return instance.getTime();
    }
}
