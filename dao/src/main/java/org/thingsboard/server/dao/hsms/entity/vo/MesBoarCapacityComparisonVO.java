package org.thingsboard.server.dao.hsms.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 生产监控VO
 *
 * @author wwj
 * @since 2021.10.21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesBoarCapacityComparisonVO {

    /**
     * 图表数据列表
     */
    @ApiModelProperty(value = "图表数据列表")
    private List<MesBoarCapacityComparisonItemVO> items;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;
}
