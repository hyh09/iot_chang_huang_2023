package org.thingsboard.server.common.data.vo.home;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.common.data.vo.resultvo.cap.AppDeviceCapVo;

import java.util.List;

/**
 * @program: springboot-jpa-20210106
 * @description: app的产能返回出参
 * @author: HU.YUNHUI
 * @create: 2021-11-09 10:55
 **/
@Data
@ToString
@ApiModel(value = "首页产量-- 查询产能的出参实体")
public class ResultHomeCapAppVo {

    @ApiModelProperty("昨天的总产量")
    private String  yesterdayValue;
    @ApiModelProperty("今天的总产量")
    private String  todayValue;
    @ApiModelProperty("历史的总产量")
    private  String history;


}
