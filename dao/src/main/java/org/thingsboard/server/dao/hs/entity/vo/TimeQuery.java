package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 时间请求参数
 *
 * @author wwj
 * @since 2021.10.26
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "时间请求参数")
public class TimeQuery {
    /**
     * 开始时间
     */
    @ApiModelProperty("开始时间")
    private Long startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty("结束时间")
    private Long endTime;
}
