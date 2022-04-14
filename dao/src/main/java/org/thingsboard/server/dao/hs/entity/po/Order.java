package org.thingsboard.server.dao.hs.entity.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.math.BigDecimal;

/**
 * 订单
 *
 * @author wwj
 * @since 2021.10.18
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "订单")
public class Order extends BasePO {

    private static final long serialVersionUID = 4934987555236873733L;

    @ApiModelProperty(value = "订单Id")
    private String id;

    @ApiModelProperty(value = "租户Id")
    private String tenantId;

    @ApiModelProperty(value = "订单编号")
    private String orderNo;

    @ApiModelProperty(value = "总数量")
    private BigDecimal total;

    @ApiModelProperty(value = "产线Id")
    private String productionLineId;

    @ApiModelProperty(value = "车间Id")
    private String workshopId;

    @ApiModelProperty(value = "工厂Id")
    private String factoryId;

    @ApiModelProperty(value = "合同号")
    private String contractNo;

    @ApiModelProperty(value = "参考合同号")
    private String refOrderNo;

    @ApiModelProperty(value = "接单时间")
    private Long takeTime;

    @ApiModelProperty(value = "客户订单号")
    private String customerOrderNo;

    @ApiModelProperty(value = "客户")
    private String customer;

    @ApiModelProperty(value = "订单类型")
    private String type;

    @ApiModelProperty(value = "经营方式")
    private String bizPractice;

    @ApiModelProperty(value = "币种")
    private String currency;

    @ApiModelProperty(value = "汇率")
    private String exchangeRate;

    @ApiModelProperty(value = "税率")
    private String taxRate;

    @ApiModelProperty(value = "税种")
    private String taxes;

    @ApiModelProperty(value = "总金额")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "单位")
    private String unit;

    @ApiModelProperty(value = "单价类型")
    private String unitPriceType;

    @ApiModelProperty(value = "附加金额")
    private BigDecimal additionalAmount;

    @ApiModelProperty(value = "付款方式")
    private String paymentMethod;

    @ApiModelProperty(value = "紧急程度")
    private String emergencyDegree;

    @ApiModelProperty(value = "工艺要求")
    private String technologicalRequirements;

    @ApiModelProperty(value = "数量")
    private BigDecimal num;

    @ApiModelProperty(value = "季节")
    private String season;

    @ApiModelProperty(value = "跟单员")
    private String merchandiser;

    @ApiModelProperty(value = "销售员")
    private String salesman;

    @ApiModelProperty(value = "短装")
    private String shortShipment;

    @ApiModelProperty(value = "溢装")
    private String overShipment;

    @ApiModelProperty(value = "备注")
    private String comment;

    @ApiModelProperty(value = "计划日期")
    private Long intendedTime;

    @ApiModelProperty(value = "标准可用时间")
    private BigDecimal standardAvailableTime;

    @ApiModelProperty(value = "是否完成")
    private Boolean isDone;
}
