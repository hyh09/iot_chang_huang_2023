package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * 看板资源
 *
 * @author wwj
 * @since 2021.11.23
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "看板资源")
public class BoardResource {

    @ApiModelProperty(value = "报警级别列表")
    private List<Map<String, String>> alarmLevelList;
}
