package org.thingsboard.server.common.data.vo.resultvo.cap;

import lombok.Data;
import lombok.ToString;

/**
 * @program: springboot-jpa-20210106
 * @description: app产能返回之设备对象
 * @author: HU.YUNHUI
 * @create: 2021-11-09 10:56
 **/
@Data
@ToString
public class AppDeviceCapVo {


    /**
     * 设备的名称
     */
    private String deviceName;
    /**
     * 设备的id
     */
    private String deviceId;
    /**
     * 产能
     */
    private String value;  //比如多个属性呢？

    /**
     * 设备所属的车间 车间名称
     */
    private String workshopName;
    /**
     * 设备所属的产线 产线名称
     */
    private String productionName;

}
