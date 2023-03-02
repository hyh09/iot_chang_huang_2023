package org.thingsboard.server.dao.hs.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.common.data.workshop.Workshop;

import java.util.Map;
import java.util.UUID;

/**
 * 设备列表归属DTO
 *
 * @author wwj
 * @since 2021.11.4
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceListAffiliationDTO {
    /**
     * 工厂
     */
    private Map<UUID, Factory> factoryMap;
    /**
     * 车间
     */
    private Map<UUID, Workshop> workshopMap;
    /**
     * 产线
     */
    private Map<UUID, ProductionLine> productionLineMap;
}
