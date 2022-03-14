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
@ApiModel(value = "历史数据图表")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryGraphVO {

    @ApiModelProperty(value = "是否展示图表")
    private Boolean enable;

    @ApiModelProperty(value = "图表名称")
    private String name;

    @ApiModelProperty(value = "数据列表")
    List<HistoryGraphPropertyVO> properties;
}
