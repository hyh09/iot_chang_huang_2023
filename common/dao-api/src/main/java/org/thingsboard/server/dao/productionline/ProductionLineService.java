package org.thingsboard.server.dao.productionline;

import org.thingsboard.server.common.data.productionline.ProductionLine;

import java.util.List;
import java.util.UUID;

public interface ProductionLineService {

    /**
     * 保存后刷新值
     * @param productionLine
     * @return
     */
    ProductionLine saveProductionLine(ProductionLine productionLine);

    /**
     * 修改后刷新值
     * @param productionLine
     * @return
     */
    ProductionLine updProductionLine(ProductionLine productionLine);

    /**
     * 删除后刷新值
     * @param id
     * @param id
     * @return
     */
    void delProductionLine(UUID id);


    /**
     * 查询租户/工厂/车间下所有生产线列表
     * @param tenantId
     * @param workshopId
     * @param factoryId
     * @return
     */
    List<ProductionLine> findProductionLineList(UUID tenantId,UUID workshopId,UUID factoryId);

    /**
     * 查询详情
     * @param id
     * @return
     */
    ProductionLine findById(UUID id);
}
