package org.thingsboard.server.common.data.vo.parameter;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.common.data.vo.AbstractDeviceVo;

import java.time.LocalDate;

/**
 * @program: thingsboard
 * @description: 产量并型图入参
 * @author: HU.YUNHUI
 * @create: 2022-02-07 10:55
 **/
@Data
@ToString
@ApiModel(value = "产量并型图入参")
public class PcTodayProportionChartOutput extends AbstractDeviceVo{

    private LocalDate date;
}


