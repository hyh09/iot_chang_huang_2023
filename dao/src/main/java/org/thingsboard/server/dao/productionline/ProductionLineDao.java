package org.thingsboard.server.dao.productionline;

import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.model.sql.ProductionLineEntity;

import java.util.List;
import java.util.UUID;

public interface ProductionLineDao extends Dao<ProductionLine>{
    ProductionLine saveProductionLine(ProductionLine productionLine);

    List<ProductionLineEntity> findProductionLineListBuyCdn(ProductionLineEntity productionLineEntity);

    /**
     * 询租户/工厂/车间下所有生产线列表
     * @param tenantId
     * @param workshopId
     * @param factoryId
     * @return
     */
    List<ProductionLine> findProductionLineList(UUID tenantId, UUID workshopId, UUID factoryId);
}
