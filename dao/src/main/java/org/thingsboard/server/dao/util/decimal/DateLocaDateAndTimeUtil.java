package org.thingsboard.server.dao.util.decimal;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @Project Name: long-win-iot
 * @File Name: DateUtil
 * @Date: 2023/1/30 15:23
 * @author: wb04
 * 业务中文描述:
 * Copyright (c) 2023,All Rights Reserved.
 */
public class DateLocaDateAndTimeUtil {
    public final static DateLocaDateAndTimeUtil INSTANCE = new DateLocaDateAndTimeUtil();


    public List<LocalDate> getMiddleDate(LocalDate begin, LocalDate end) {
        List<LocalDate> localDateList = new ArrayList<>();
        long length = end.toEpochDay() - begin.toEpochDay();
        for (long i = length; i >= 0; i--) {
            localDateList.add(end.minusDays(i));
        }
        return localDateList;
    }


}
