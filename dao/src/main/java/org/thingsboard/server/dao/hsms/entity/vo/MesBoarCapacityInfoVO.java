package org.thingsboard.server.dao.hsms.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 产量信息VO
 *
 * @author wwj
 * @since 2021.10.21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesBoarCapacityInfoVO {

    /**
     * 本月计划
     */
    @ApiModelProperty(value = "本月计划")
    private BigDecimal monthPlan;

    /**
     * 本月产量
     */
    @ApiModelProperty(value = "本月产量")
    private BigDecimal monthCapacity;

    /**
     * 完成率
     */
    @ApiModelProperty(value = "完成率")
    private BigDecimal rate;

    /**
     * 当日产量
     */
    @ApiModelProperty(value = "当日产量")
    private BigDecimal todayCapacity;

    /**
     * 正生产数
     */
    @ApiModelProperty(value = "正生产数")
    private BigDecimal productionNum;

    /**
     * 回修数量
     */
    @ApiModelProperty(value = "回修数量")
    private BigDecimal repairNum;
}
