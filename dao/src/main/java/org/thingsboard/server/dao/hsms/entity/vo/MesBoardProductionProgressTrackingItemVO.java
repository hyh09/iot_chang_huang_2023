package org.thingsboard.server.dao.hsms.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 机台产量对比VO
 *
 * @author wwj
 * @since 2021.10.21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesBoardProductionProgressTrackingItemVO {

    /**
     * 卡号
     */
    @ApiModelProperty(value = "卡号")
    private String cardNo;

    /**
     * 客户
     */
    @ApiModelProperty(value = "客户")
    private String customer;

    /**
     * 色号
     */
    @ApiModelProperty(value = "色号")
    private String color;

    /**
     * 超时
     */
    @ApiModelProperty(value = "超时")
    private String overTime;

    /**
     * 当前工序
     */
    @ApiModelProperty(value = "当前工序")
    private String nowProcess;

    /**
     * 上一道工序
     */
    @ApiModelProperty(value = "上一道工序")
    private String lastProcess;

    /**
     * 上工序完成时间
     */
    @ApiModelProperty(value = "上工序完成时间")
    private String finishedTime;
}
