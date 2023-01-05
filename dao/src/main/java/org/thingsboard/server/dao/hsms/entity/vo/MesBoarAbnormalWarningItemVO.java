package org.thingsboard.server.dao.hsms.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 异常预警VO
 *
 * @author wwj
 * @since 2021.10.21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesBoarAbnormalWarningItemVO {

    /**
     * 预警信息
     */
    @ApiModelProperty(value = "预警信息")
    private String info;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    private String startTime;
}
