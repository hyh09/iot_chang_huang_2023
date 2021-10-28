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
 * 报警信息请求参数
 *
 * @author wwj
 * @since 2021.10.26
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "报警信息请求参数")
public class AlarmRecordQuery extends FactoryDeviceQuery{
    /**
     * 报警状态
     */
    @ApiModelProperty("报警状态")
    private AlarmSimpleStatus alarmSimpleStatus;

    /**
     * 报警级别
     */
    @ApiModelProperty("报警级别")
    private AlarmSimpleLevel alarmSimpleLevel;
}
