package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 看板报警
 *
 * @author wwj
 * @since 2021.10.26
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "看板报警")
public class BoardAlarmResult {

    /**
     * 报警次数列表
     */
    @ApiModelProperty("报警次数列表")
    private List<BoardAlarmTimesResult> timesResultList;

    /**
     * 占比
     */
    @ApiModelProperty(value = "占比",notes = "仅长胜、工厂看板")
    private BoardAlarmLevelProportionResult proportionResult;
}
