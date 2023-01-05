package org.thingsboard.server.dao.hsms.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 设备开机率分析VO
 *
 * @author wwj
 * @since 2021.10.21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesBoarDeviceOperationRateVO {

    /**
     * 开机率(百分比,保留两位小数)
     */
    @ApiModelProperty(value = "开机率(百分比,保留两位小数)")
    private BigDecimal rate;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 开机时长
     */
    @ApiModelProperty(value = "开机时长", notes = "不显示")
    private Long time;

    /**
     * id
     */
    @ApiModelProperty(value = "id", notes = "不显示")
    private UUID id;
}
