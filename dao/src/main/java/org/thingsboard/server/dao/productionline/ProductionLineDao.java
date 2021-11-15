package org.thingsboard.server.dao.productionline;

import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.dao.Dao;

import java.util.List;
import java.util.UUID;

public interface ProductionLineDao extends Dao<ProductionLine>{
    ProductionLine saveProductionLine(ProductionLine productionLine)throws ThingsboardException;

    List<ProductionLine> findProductionLineListBuyCdn(ProductionLine productionLine);

    /**
     * 询租户/工厂/车间下所有生产线列表
     * @param tenantId
     * @param workshopId
     * @param factoryId
     * @return
     */
    List<ProductionLine> findProductionLineList(UUID tenantId, UUID workshopId, UUID factoryId);

    /**
     * 根据id删除（逻辑删除）
     * @param id
     */
    void delProductionLine(UUID id);

    /**
     * 根据车间id删除（逻辑删除）
     * @param workshopId
     */
    void delProductionLineByWorkshopId(UUID workshopId);

    /**
     * 批量查询
     * @param ids
     * @return
     */
    List<ProductionLine> getProductionLineByIdList(List<UUID> ids);
}
