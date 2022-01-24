package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.UUID;

/**
 * 订单/订单产能/App产能监控列表请求参数
 *
 * @author wwj
 * @since 2021.10.26
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "订单/订单产能/App产能监控列表请求参数")
public class OrderListQuery {
    /**
     * 订单号
     */
    @ApiModelProperty("订单号")
    private String orderNo;

    /**
     * 工厂名
     */
    @ApiModelProperty("工厂名")
    private String factoryName;

    /**
     * 工厂Id
     */
    @ApiModelProperty("工厂Id")
    private UUID factoryId;

    /**
     * 订单类型
     */
    @ApiModelProperty("订单类型")
    private String type;
}
