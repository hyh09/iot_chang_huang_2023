package org.thingsboard.server.dao.hsms.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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
public class MesBoardCapacityTrendItemVO {

    /**
     * y轴数据,产量
     */
    @ApiModelProperty(value = "y轴数据,产量")
    @JsonProperty("yValue")
    private BigDecimal yValue;

    /**
     * x轴数据
     */
    @ApiModelProperty(value = "日期")
    @JsonProperty("xValue")
    private String xValue;
}
