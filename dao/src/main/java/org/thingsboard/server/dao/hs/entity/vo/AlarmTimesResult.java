package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 实时监控预警数据
 *
 * @author wwj
 * @since 2021.10.26
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "实时监控预警数据")
public class AlarmTimesResult {

    /**
     * 预警时间
     */
    @ApiModelProperty("时间")
    private String time;

    /**
     * 预警次数
     */
    @ApiModelProperty("预警次数")
    private Integer num;
}
