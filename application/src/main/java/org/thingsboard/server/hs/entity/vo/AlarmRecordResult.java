package org.thingsboard.server.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.thingsboard.server.common.data.alarm.AlarmSeverity;
import org.thingsboard.server.common.data.alarm.AlarmStatus;
import org.thingsboard.server.hs.entity.enums.AlarmSimpleLevel;
import org.thingsboard.server.hs.entity.enums.AlarmSimpleStatus;

/**
 * 报警信息结果
 *
 * @author wwj
 * @since 2021.10.26
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "报警信息结果")
public class AlarmRecordResult {
    /**
     * id
     */
    @ApiModelProperty("id")
    private String id;
    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Long createTime;
    /**
     * 设备名称
     */
    @ApiModelProperty("设备名称")
    private String name;
    /**
     * 报警标题
     */
    @ApiModelProperty("报警标题")
    private String title;
    /**
     * 报警信息
     */
    @ApiModelProperty("报警信息")
    private String info;
    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private AlarmSimpleStatus status;
    /**
     * 级别
     */
    @ApiModelProperty("级别")
    private AlarmSimpleLevel level;

}
