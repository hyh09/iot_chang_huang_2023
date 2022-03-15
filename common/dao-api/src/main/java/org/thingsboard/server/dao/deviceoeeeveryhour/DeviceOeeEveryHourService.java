package org.thingsboard.server.dao.deviceoeeeveryhour;

import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.statisticoee.StatisticOee;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface DeviceOeeEveryHourService {

    /**
     * 查询OEE计算历史，返回每小时的值
     * @return
     */
    List<StatisticOee> getStatisticOeeEveryHourList(StatisticOee statisticOee) throws ThingsboardException;

    /**
     * 查询OEE实时数据，返回每小时的值
     * @return
     */
    List<StatisticOee> getStatisticOeeListByRealTime(StatisticOee statisticOee) throws ThingsboardException;


    /**
     * 查询设备当天OEE
     * 设备当天OEE需班次时间结束后运算，当天班次未结束取前一天的值
     *
     * @param deviceId
     * @return
     */
    BigDecimal getStatisticOeeDeviceByCurrentDay(UUID deviceId);

    /**
     * 手动执行当天所有设备每小时OEE同步
     */
    void statisticOeeByTimedTask();

    /**
     * 执行（指定时间区间）所有设备每小时OEE同步
     * @param statisticOee
     * @return
     * @throws ThingsboardException
     */
    void statisticOeeByAnyTime(StatisticOee statisticOee) throws ThingsboardException;

}
