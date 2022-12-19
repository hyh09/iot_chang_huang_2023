package org.thingsboard.server.dao.hsms.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.thingsboard.server.dao.hs.entity.po.BasePO;
import org.thingsboard.server.dao.hsms.entity.enums.DictDevicePropertySwitchEnum;

import java.util.UUID;

/**
 * 设备参数筛选-设备详情
 *
 * @author wwj
 * @since 2021.10.21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictDevicePropertySwitchVO extends BasePO {

    private static final long serialVersionUID = 4134987555236813704L;

    /**
     * 设备Id
     */
    @ApiModelProperty(value = "设备Id", notes = "仅用于展示")
    private UUID deviceId;

    /**
     * 设备字典Id
     */
    @ApiModelProperty(value = "设备字典Id", notes = "仅用于展示")
    private UUID dictDeviceId;

    /**
     * 属性Id
     */
    @ApiModelProperty(value = "属性Id", required = true)
    private UUID propertyId;

    /**
     * 属性类型
     */
    @ApiModelProperty(value = "属性类型", required = true)
    private String propertyType;

    /**
     * 属性开关
     */
    @ApiModelProperty(value = "属性开关", required = true)
    private DictDevicePropertySwitchEnum propertySwitch;
}
