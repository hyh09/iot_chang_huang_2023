package org.thingsboard.server.common.data.vo.tskv.consumption;

import lombok.Data;
import lombok.ToString;

/**
 * @program: thingsboard
 * @description:
 * @author: HU.YUNHUI
 * @create: 2021-12-08 14:23
 **/
@Data
@ToString
public class TkTodayVo {

    private  String deviceName;

    private  String deviceId;

    private  String value;

    private  String totalValue;

    private  Long ts;
}
