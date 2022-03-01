package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "历史数据图表属性")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryGraphPropertyVO {

    @ApiModelProperty(value = "是否展示该属性")
    private Boolean isShowChart;

    @ApiModelProperty(value = "属性标题")
    private String title;

    @ApiModelProperty(value = "属性名称")
    private String name;

    @ApiModelProperty(value = "属性单位")
    private String unit;

    @ApiModelProperty(value = "时序数据列表")
    List<HistoryGraphPropertyTsKvVO> tsKvs;
}
