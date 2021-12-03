package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * app首页报警数据
 *
 * @author wwj
 * @since 2021.12.3
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "app首页报警数据")
public class AppIndexAlarmResult {
    /**
     * 今日预警次数
     */
    @ApiModelProperty("今日预警次数")
    private Integer todayAlarmTimes;

    /**
     * 昨日预警次数
     */
    @ApiModelProperty("昨日预警次数")
    private Integer yesterdayAlarmTimes;

    /**
     * 历史预警次数
     */
    @ApiModelProperty("历史预警次数")
    private Integer historyAlarmTimes;

}
