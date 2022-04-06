package org.thingsboard.server.dao.hs.service;


import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.ota.ChecksumAlgorithm;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.hs.dao.OrderPlanEntity;
import org.thingsboard.server.dao.hs.entity.po.OrderPlan;
import org.thingsboard.server.dao.hs.entity.vo.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * 订单接口
 *
 * @author wwj
 * @since 2021.10.18
 */
public interface OrderService {

    /**
     * 根据设备id以及实际时间查询订单信息
     * @param deviceIds
     * @param startTime
     * @param endTime
     * @return
     */
    List<OrderPlanEntity> findDeviceAchieveOrPlanList(List<UUID> deviceIds, Long startTime, Long endTime);

    /**
     * 查询时间范围内的实际产量
     * @param factoryIds
     * @param startTime
     * @param endTime
     * @return
     */
    BigDecimal findActualByFactoryIds(UUID factoryIds, Long startTime, Long endTime);

    /**
     * 查询时间范围内的计划产量
     * @param factoryIds
     * @param startTime
     * @param endTime
     * @return
     */
    BigDecimal findIntendedByFactoryIds(UUID factoryIds,Long startTime, Long endTime);

    /**
     * 查询时间范围内的车间计划产量
     * @param deviceId
     * @param startTime
     * @param endTime
     * @return
     */
    String findIntendedByDeviceId(UUID deviceId, Long startTime, Long endTime);

    /**
     *查询时间范围内的车间实际产量
     * @param deviceId
     * @param startTime
     * @param endTime
     * @return
     */
    String findActualByDeviceId(UUID deviceId, Long startTime, Long endTime);
}
