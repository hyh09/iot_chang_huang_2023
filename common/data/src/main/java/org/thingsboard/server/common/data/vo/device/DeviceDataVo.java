package org.thingsboard.server.common.data.vo.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 设备返回的属性
 * @author: HU.YUNHUI
 * @create: 2021-11-11 18:24
 **/
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DeviceDataVo {

    private  UUID  deviceId;

    private   String deviceName;

    private  UUID factoryId;

    private  String  factoryName;


    private UUID workshopId;

    private  String workshopName;


    private  UUID productionLineId;

    private  String  productionLineName;

}
