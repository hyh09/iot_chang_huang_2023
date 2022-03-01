package org.thingsboard.server.common.data.vo.bodrd;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @program: thingsboard
 * @description: 今天, 总产能，历史总产能
 * @author: HU.YUNHUI
 * @create: 2022-03-01 14:53
 **/
@Data
@ToString
@ApiModel(value = "今天, 总产能，历史总产能")
public class TodaySectionHistoryVo {

    @ApiModelProperty("昨天的总产量")
    private String  todayValue="0";
    @ApiModelProperty("总产能")
    private String  sectionValue="0";
    @ApiModelProperty("历史的总产量")
    private  String historyValue="0";
}
