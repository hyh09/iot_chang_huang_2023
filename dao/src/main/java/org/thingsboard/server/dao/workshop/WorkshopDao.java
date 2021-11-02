package org.thingsboard.server.dao.workshop;

import org.thingsboard.server.common.data.workshop.Workshop;
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.model.sql.WorkshopEntity;

import java.util.List;
import java.util.UUID;

public interface WorkshopDao extends Dao<Workshop>{

    Workshop saveWorkshop(Workshop workshop);

    List<WorkshopEntity> findWorkshopListBuyCdn(WorkshopEntity workshopEntity);

    /**
     * 查询租户下所有车间列表
     * @param tenantId
     * @param factoryId
     * @return
     */
    List<Workshop> findWorkshopListByTenant(UUID tenantId, UUID factoryId);

}
