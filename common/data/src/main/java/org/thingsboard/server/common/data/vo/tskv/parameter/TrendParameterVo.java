package org.thingsboard.server.common.data.vo.tskv.parameter;

import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.common.data.user.DefalutSvc;

import java.util.UUID;

/**
 * @program: thingsboard
 * @description:
 * @author: HU.YUNHUI
 * @create: 2021-12-14 16:09
 **/
@Data
@ToString
public class TrendParameterVo {

    /**
     * 当前登录人的租户id
     */
    private UUID  tenantId;

    private  UUID factoryId;

    private  UUID workshopId;

    private  Long startTime;

    private  Long  endTime;

    private  String key;



}
