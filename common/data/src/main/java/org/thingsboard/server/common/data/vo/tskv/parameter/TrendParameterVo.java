package org.thingsboard.server.common.data.vo.tskv.parameter;

import lombok.Data;
import lombok.ToString;

/**
 * @program: thingsboard
 * @description:
 * @author: HU.YUNHUI
 * @create: 2021-12-14 16:09
 **/
@Data
@ToString
public class TrendParameterVo {

    private  Long startTime;

    private  Long  endTime;

    private  String key;
}
