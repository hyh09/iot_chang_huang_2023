package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 订单/订单产能列表返回结果
 *
 * @author wwj
 * @since 2021.10.26
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "订单/订单产能列表返回结果")
public class OrderListResult {
    @ApiModelProperty(value = "订单Id")
    private String id;

    @ApiModelProperty(value = "订单号", notes = "通用显示字段")
    private String orderNo;

    @ApiModelProperty(value = "工厂名", notes = "通用显示字段")
    private String factoryName;

    @ApiModelProperty(value = "紧急程度", notes = "通用显示字段")
    private String emergencyDegree;

    @ApiModelProperty(value = "跟单员", notes = "订单显示字段")
    private String merchandiser;

    @ApiModelProperty(value = "销售员", notes = "订单显示字段")
    private String salesman;

    @ApiModelProperty(value = "计划日期", notes = "通用显示字段")
    private Long intendedTime;

    @ApiModelProperty(value = "创建人", notes = "通用显示字段")
    private String creator;

    @ApiModelProperty(value = "创建时间", notes = "通用显示字段")
    private Long createdTime;

    @ApiModelProperty(value = "订单金额", notes = "产能监控显示字段")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "总数量", notes = "产能监控显示字段")
    private BigDecimal total;

    @ApiModelProperty(value = "完成产量", notes = "产能监控显示字段")
    private BigDecimal capacities;

    @ApiModelProperty(value = "完成度", notes = "产能监控显示字段")
    private BigDecimal completeness;

}
