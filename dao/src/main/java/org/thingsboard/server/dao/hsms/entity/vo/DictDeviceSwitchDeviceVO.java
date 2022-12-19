package org.thingsboard.server.dao.hsms.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.thingsboard.server.dao.hs.entity.po.BasePO;

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
public class DictDeviceSwitchDeviceVO extends BasePO {

    private static final long serialVersionUID = 4134987555236813704L;

    /**
     * 设备Id
     */
    @ApiModelProperty(value = "设备Id")
    private UUID deviceId;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    /**
     * 工厂名称
     */
    @ApiModelProperty(value = "工厂名称")
    private String factoryName;

    /**
     * 车间名称
     */
    @ApiModelProperty(value = "车间名称")
    private String workshopName;

    /**
     * 产线名称
     */
    @ApiModelProperty(value = "产线名称")
    private String productionLineName;
}
