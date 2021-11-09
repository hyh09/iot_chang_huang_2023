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
@ApiModel(value = "App历史数据VO")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppHistoryVO {

    @ApiModelProperty(value = "是否展示图表", notes = "仅数值型数据展示图表")
    private Boolean isShowChart;

    @ApiModelProperty(value = "数据列表")
    List<DictDeviceGroupPropertyVO> propertyVOList;
}
