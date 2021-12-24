package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 看板报警级别占比结果
 *
 * @author wwj
 * @since 2021.10.26
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "看板报警级别占比结果")
public class BoardAlarmLevelProportionResult {
    /**
     * 总数
     */
    @ApiModelProperty("总数")
    private Integer count;

    /**
     * 危险警报总数
     */
    @ApiModelProperty("危险警报总数")
    private Integer criticalCount;

    /**
     * 重要警报总数
     */
    @ApiModelProperty("重要警报总数")
    private Integer majorCount;

    /**
     * 次要警报总数
     */
    @ApiModelProperty("次要警报总数")
    private Integer minorCount;

    /**
     * 警告警报总数
     */
    @ApiModelProperty("警告警报总数")
    private Integer warningCount;

    /**
     * 不确定警报总数
     */
    @ApiModelProperty("不确定警报总数")
    private Integer indeterminateCount;
}
