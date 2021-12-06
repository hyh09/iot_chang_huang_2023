package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 工厂设备请求参数
 *
 * @author wwj
 * @since 2021.10.26
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "工厂设备请求参数", description = "都不传的话查询未分配的设备")
public class FactoryDeviceQuery {
    /**
     * 工厂Id
     */
    @ApiModelProperty("工厂Id")
    private String factoryId;

    /**
     * 车间Id
     */
    @ApiModelProperty("车间Id")
    private String workshopId;

    /**
     * 产线Id
     */
    @ApiModelProperty("产线Id")
    private String productionLineId;

    /**
     * 设备Id
     */
    @ApiModelProperty("设备Id")
    private String deviceId;

    /**
     * 是否查询全部
     */
    @ApiModelProperty("是否查询全部")
    private Boolean isQueryAll;

    public FactoryDeviceQuery(String factoryId, String workshopId, String productionLineId, String deviceId) {
        this.factoryId = factoryId;
        this.workshopId = workshopId;
        this.productionLineId = productionLineId;
        this.deviceId = deviceId;
        this.isQueryAll = Boolean.FALSE;
    }

    public boolean isQueryFactoryOnly() {
        return deviceId == null && productionLineId == null && workshopId == null && Boolean.FALSE.equals(isQueryAll);
    }

    public boolean isQueryWorkshopOnly() {
        return deviceId == null && productionLineId == null && factoryId == null && Boolean.FALSE.equals(isQueryAll);
    }

    public static FactoryDeviceQuery newQueryAllEntity() {
        return new FactoryDeviceQuery(null, null, null, null, Boolean.TRUE);
    }
}
