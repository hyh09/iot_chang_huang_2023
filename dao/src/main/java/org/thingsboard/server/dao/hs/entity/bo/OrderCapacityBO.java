package org.thingsboard.server.dao.hs.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.compress.utils.Lists;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * 订单产量 BO
 *
 * @author wwj
 * @since 2021.10.26
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
public class OrderCapacityBO {

    /**
     * 产量
     */
    private BigDecimal capacities;

    /**
     * 订单Id
     */
    private UUID orderId;

    /**
     * 设备产量列表
     */
    private List<OrderDeviceCapacityBO> deviceCapacities;

    public OrderCapacityBO() {
        super();
        deviceCapacities = Lists.newArrayList();
    }
}
