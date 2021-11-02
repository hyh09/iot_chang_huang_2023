package org.thingsboard.server.dao.hs.utils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * 工具类
 *
 * @author wwj
 * @since 2021.10.21
 */
public class CommonUtil {

    /**
     * 获得近几个月的开始时间
     *
     * @param monthNum 月份数量
     */
    public static List<Long> listLatestMonthsStartTime(int monthNum) {
        List<Long> temp = new ArrayList<>();
        if (monthNum < 1) {
            return new ArrayList<>();
        }
        for (int i = 1; i <= monthNum; i++) {
            temp.add(YearMonth.now().minusMonths(i).atDay(1).atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli());
        }
        return temp;
    }

    /**
     * 获得当天零点的时间
     */
    public static Long getTodayStartTime() {
        return LocalDate.now().atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }
}
