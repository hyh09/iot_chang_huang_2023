package org.thingsboard.server.common.data.vo.tskv.parameter;

import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.common.data.validation.CheckV3queryChartGroup;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;

import javax.validation.constraints.NotEmpty;
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

    private UUID  dictDeviceId;


    private  Long startTime;

    private  Long  endTime;

    @NotEmpty(message = "入参key不能为空,",groups = CheckV3queryChartGroup.class)
    private  String key;



    public QueryTsKvVo toQueryTsKvVo()
    {
        QueryTsKvVo  vo = new QueryTsKvVo();
        vo.setTenantId(this.tenantId);
        vo.setFactoryId(this.factoryId);
        vo.setWorkshopId(this.workshopId);
        vo.setDictDeviceId(this.dictDeviceId);
//        vo.setProductionLineId(null);
//        vo.setDeviceId(this.deviceId);
        return  vo;

    }



}
