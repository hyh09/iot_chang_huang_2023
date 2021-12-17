package org.thingsboard.server.common.data.vo.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 设备返回的属性
 * @author: HU.YUNHUI
 * @create: 2021-11-11 18:24
 **/

public interface DeviceDataSvc {
//(t.id,t.name,t.code,f1.id,f1.name,t.workshopId,w1.name,t.productionLineId,p1.name,t.picture)


    public String getId();

    public String getName();

    public String getCode();

    public String getFactoryId();

    public String getFactoryName();


    public String getWorkshopId();


    public String getWorkshopName();

    public String getProductionLineId();

    public String getProductionLineName();

    public String getPicture();

}
