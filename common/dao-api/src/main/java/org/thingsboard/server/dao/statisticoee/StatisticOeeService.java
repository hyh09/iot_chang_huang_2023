package org.thingsboard.server.dao.statisticoee;

import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.statisticoee.StatisticOee;

import java.util.List;

public interface StatisticOeeService {

    /**
     * OEE计算，返回每小时的值
     * @return
     */
    List<StatisticOee> getStatisticOeeList(StatisticOee statisticOee) throws ThingsboardException;


}
