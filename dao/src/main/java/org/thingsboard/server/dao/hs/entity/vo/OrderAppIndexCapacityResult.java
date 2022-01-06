package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 订单首页产能监控
 *
 * @author wwj
 * @since 2021.10.18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "订单首页产能监控")
public class OrderAppIndexCapacityResult {

    @ApiModelProperty(value = "订单数量")
    private Long num;

    @ApiModelProperty(value = "完成度")
    private BigDecimal completeness;
}
