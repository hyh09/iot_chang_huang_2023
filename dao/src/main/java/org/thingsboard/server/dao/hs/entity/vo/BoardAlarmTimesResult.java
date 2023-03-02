package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "看板报警次数")
public class BoardAlarmTimesResult {
    /**
     * 预警图标x轴显示值
     */
    @ApiModelProperty("预警图标x轴显示值")
    private String value;

    /**
     * 预警次数
     */
    @ApiModelProperty("预警次数")
    private Integer num;
}
