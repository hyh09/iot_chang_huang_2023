package org.thingsboard.server.dao.hsms.entity.po;

import lombok.*;
import org.thingsboard.server.dao.hs.entity.enums.DictDevicePropertyTypeEnum;
import org.thingsboard.server.dao.hs.entity.po.BasePO;
import org.thingsboard.server.dao.hsms.entity.enums.DictDevicePropertySwitchEnum;

import java.util.UUID;

/**
 * 设备字典-属性开关
 *
 * @author wwj
 * @since 2021.10.21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictDeviceSwitch extends BasePO {

    private static final long serialVersionUID = 4134987555236873705L;
    /**
     * 设备字典-属性开关Id
     */
    private UUID id;

    /**
     * 设备Id
     */
    private UUID deviceId;

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
     * 属性开关
     */
    private DictDevicePropertySwitchEnum propertySwitch;
}
