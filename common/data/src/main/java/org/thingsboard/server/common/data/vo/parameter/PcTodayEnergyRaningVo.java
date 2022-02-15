package org.thingsboard.server.common.data.vo.parameter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.common.data.vo.AbstractDeviceVo;
import org.thingsboard.server.common.data.vo.QueryTsKvVo;

import java.time.LocalDate;

/**
 * @program: thingsboard
 * @description: 今日能耗排行
 * @author: HU.YUNHUI
 * @create: 2022-01-19 14:07
 **/
@Data
@ToString
@ApiModel(value = "设备入参实体")
public class PcTodayEnergyRaningVo extends AbstractDeviceVo{

    private LocalDate date;

    @ApiModelProperty("1:水 2 气  3电")
    private  String  keyNum;


    public QueryTsKvVo toQueryTsKvVo()
    {
        QueryTsKvVo  vo = new QueryTsKvVo();
        vo.setTenantId(this.tenantId);
        vo.setFactoryId(this.factoryId);
        vo.setWorkshopId(this.workshopId);
        vo.setProductionLineId(this.productionLineId);
        vo.setDeviceId(this.deviceId);
        return  vo;

    }

}
