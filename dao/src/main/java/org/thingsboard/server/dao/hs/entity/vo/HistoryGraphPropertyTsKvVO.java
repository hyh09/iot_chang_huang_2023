package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.thingsboard.server.dao.hs.entity.bo.GraphTsKv;

@Data
@Accessors(chain = true)
@ApiModel(value = "历史数据图表属性时序值")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryGraphPropertyTsKvVO implements GraphTsKv {

    @ApiModelProperty(value = "时间")
    private Long ts;

    @ApiModelProperty(value = "值")
    private String value;
}
