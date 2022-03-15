package org.thingsboard.server.dao.deviceoeeeveryhour;

import org.thingsboard.server.common.data.deviceoeeeveryhour.DeviceOeeEveryHour;
import org.thingsboard.server.common.data.exception.ThingsboardException;

import java.util.List;

public interface DeviceOeeEveryHourDao {

    /**
     * 保存
     * @param deviceOeeEveryHour
     * @return
     */
    void save(DeviceOeeEveryHour deviceOeeEveryHour) throws ThingsboardException;

    List<DeviceOeeEveryHour> findAllByCdn(DeviceOeeEveryHour deviceOeeEveryHour, String orderBy, String orderByValue);
}