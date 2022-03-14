package org.thingsboard.server.dao.productioncalender;

import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.productioncalender.ProductionCalender;

import java.util.List;
import java.util.UUID;

public interface ProductionCalenderDao {

    /**
     * 保存
     *
     * @param productionCalender
     * @return
     */
    void saveProductionCalender(ProductionCalender productionCalender) throws ThingsboardException;

    /**
     * 删除
     *
     * @param id
     * @param id
     * @return
     */
    void delProductionCalender(UUID id) throws ThingsboardException;

    /**
     * 查询详情
     *
     * @param id
     * @return
     */
    ProductionCalender findById(UUID id);

    /**
     * 分页查询
     *
     * @param pageLink
     * @return
     */
    PageData<ProductionCalender> findProductionCalenderPage(ProductionCalender productionCalender, PageLink pageLink);

    /**
     * 查询设备历史生产日历记录列表
     *
     * @param deviceId
     * @param pageLink
     * @return
     */
    PageData<ProductionCalender> getHistoryPageByDeviceId(UUID deviceId, PageLink pageLink);

    /**
     * 设备生产日历历史记录列表
     *
     * @param deviceId
     * @return
     */
    List<ProductionCalender> getHistoryByDeviceId(UUID deviceId);

    /**
     * 查询设备某时间区间内班次
     * @param deviceId
     * @return
     */
    List<ProductionCalender> getDeviceByTimenterval(UUID deviceId,long startTime,long endTime);
}