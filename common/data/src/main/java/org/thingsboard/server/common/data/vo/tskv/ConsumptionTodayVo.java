package org.thingsboard.server.common.data.vo.tskv;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.common.data.vo.tskv.consumption.TkTodayVo;

import java.util.List;

/**
 * @program: thingsboard
 * @description:
 * @author: HU.YUNHUI
 * @create: 2021-12-08 14:22
 **/
@Data
@ToString
@ApiModel(value = "看板的-- 设备今日耗能量")
public class ConsumptionTodayVo {

    @ApiModelProperty("水的今日能耗 top排行由大到小的")
    private    List<TkTodayVo> waterList;
    @ApiModelProperty("电的今日能耗 top排行由大到小的")
    private    List<TkTodayVo> electricList;
    @ApiModelProperty("气的今日能耗 top排行由大到小的")
    private    List<TkTodayVo> gasList;
}
