package org.thingsboard.server.dao.hs.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.common.data.workshop.Workshop;

/**
 * 设备基础信息DTO
 *
 * @author wwj
 * @since 2021.11.3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceBaseDTO {
    /**
     * 工厂
     */
    private Factory factory;
    /**
     * 车间
     */
    private Workshop workshop;
    /**
     * 产线
     */
    private ProductionLine productionLine;
    /**
     * 设备
     */
    private Device device;
}
