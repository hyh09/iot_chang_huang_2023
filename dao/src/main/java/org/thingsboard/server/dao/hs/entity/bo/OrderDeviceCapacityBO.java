package org.thingsboard.server.dao.hs.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 订单设备产量 BO
 *
 * @author wwj
 * @since 2021.10.26
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrderDeviceCapacityBO {

    /**
     * 产量
     */
    private BigDecimal capacities;

    /**
     * 设备计划Id
     */
    private UUID planId;

    /**
     * 是否参与产能计算
     */
    private Boolean enabled;
}
