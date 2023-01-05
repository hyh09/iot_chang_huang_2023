package org.thingsboard.server.dao.hsms.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 产量趋势VO
 *
 * @author wwj
 * @since 2021.10.21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesBoarCapacityTrendItemVO {

    /**
     * y轴数据,产量
     */
    @ApiModelProperty(value = "y轴数据,产量")
    private BigDecimal yValue;

    /**
     * x轴数据
     */
    @ApiModelProperty(value = "x轴数据")
    private String xValue;
}
