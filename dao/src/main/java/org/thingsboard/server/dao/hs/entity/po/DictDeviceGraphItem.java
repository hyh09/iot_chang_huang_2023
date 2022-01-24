package org.thingsboard.server.dao.hs.entity.po;

import lombok.*;
import org.thingsboard.server.dao.hs.entity.enums.DictDevicePropertyTypeEnum;

import java.util.UUID;

/**
 * 设备字典-图表分项
 *
 * @author wwj
 * @since 2021.10.21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictDeviceGraphItem extends BasePO {

    private static final long serialVersionUID = 4134987555236873704L;
    /**
     * 设备字典-图表分项Id
     */
    private UUID id;

    /**
     * 设备字典-图表Id
     */
    private UUID graphId;

    /**
     * 设备字典Id
     */
    private UUID dictDeviceId;

    /**
     * 属性Id
     */
    private UUID propertyId;

    /**
     * 属性类型
     */
    private DictDevicePropertyTypeEnum propertyType;

    /**
     * 排序
     */
    private Integer sort;
}
