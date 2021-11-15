package org.thingsboard.server.common.data.vo.home;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

/**
 * @program: thingsboard
 * @description: 能耗 昨天今天历史的数据
 * @author: HU.YUNHUI
 * @create: 2021-11-12 15:29
 **/
@Data
@ToString
@ApiModel(value = "首页产量-- 查询产能的出参实体")
public class ResultHomeEnergyAppVo {

    @ApiModelProperty("昨天的能耗")
    private Map<String,String> yesterdayValue;
    @ApiModelProperty("今天的能耗")
    private Map<String,String>  todayValue;
    @ApiModelProperty("历史的能耗")
    private  Map<String,String> history;
}
