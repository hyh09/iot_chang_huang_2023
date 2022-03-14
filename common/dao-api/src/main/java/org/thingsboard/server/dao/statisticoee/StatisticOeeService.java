package org.thingsboard.server.dao.statisticoee;

import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.statisticoee.StatisticOee;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface StatisticOeeService {

    /**
     * OEE计算，返回每小时的值
     * @return
     */
    List<StatisticOee> getStatisticOeeEveryHourList(StatisticOee statisticOee) throws ThingsboardException;

    /**
     * 查询设备当天OEE
     * 设备当天OEE需班次时间结束后运算，当天班次未结束取前一天的值
     *
     * @param deviceId
     * @return
     */
    BigDecimal getStatisticOeeDeviceByCurrentDay(UUID deviceId);
}
