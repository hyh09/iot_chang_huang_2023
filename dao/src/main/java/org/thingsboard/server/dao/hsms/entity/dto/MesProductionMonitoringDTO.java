package org.thingsboard.server.dao.hsms.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Mes 生产监控数据
 *
 * @author wwj
 * @since 2021.10.21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesProductionMonitoringDTO {

    private static final long serialVersionUID = 4134987555236813704L;

    /**
     * 日期
     */
    @ApiModelProperty(value = "date")
    private String date;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "deviceName")
    private String deviceName;

    /**
     * 产量
     */
    @ApiModelProperty(value = "capacity")
    private BigDecimal capacity;
}
