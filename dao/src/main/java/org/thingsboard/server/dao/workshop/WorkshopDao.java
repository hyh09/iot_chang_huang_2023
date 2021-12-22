package org.thingsboard.server.dao.workshop;

import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.workshop.Workshop;
import org.thingsboard.server.dao.Dao;

import java.util.List;
import java.util.UUID;

public interface WorkshopDao extends Dao<Workshop>{

    Workshop saveWorkshop(Workshop workshop)throws ThingsboardException;

    List<Workshop> findWorkshopListByCdn(Workshop workshop);

    List<Workshop> findWorkshopListByfactoryId(UUID factoryId);

    /**
     * 查询租户下所有车间列表
     * @param tenantId
     * @param factoryId
     * @return
     */
    List<Workshop> findWorkshopListByTenant(UUID tenantId, UUID factoryId);

    /**
     * 删除(逻辑删除)
     * @param id
     */
    void delWorkshop(UUID id) throws ThingsboardException ;

    /**
     * 根据工厂删除后刷新值(逻辑删除)
     * @param factoryId
     */
    void delWorkshopByFactoryId(UUID factoryId);

    /**
     * 批量查询
     * @param ids
     * @return
     */
    List<Workshop> getWorkshopByIdList(List<UUID> ids);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    Workshop findById(UUID id);
}
