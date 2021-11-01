package org.thingsboard.server.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * 报警记录资源
 *
 * @author wwj
 * @since 2021.10.26
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "报警记录资源")
public class AlarmRecordResource {

    @ApiModelProperty(value = "报警状态列表")
    private List<Map<String, String>> alarmStatusList;

    @ApiModelProperty(value = "报警级别列表")
    private List<Map<String, String>> alarmLevelList;
}
