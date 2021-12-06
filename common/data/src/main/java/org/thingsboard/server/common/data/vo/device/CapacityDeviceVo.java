package org.thingsboard.server.common.data.vo.device;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 产能运算配置返回
 * @author: HU.YUNHUI
 * @create: 2021-12-06 14:19
 **/
@Data
@ToString
public class CapacityDeviceVo {

    private Boolean flg;

    private UUID tenantId;

    private UUID factoryId;

    private UUID workshopId;



    private UUID productionLineId;

    private  UUID deviceId;

    private  String deviceName;

    private  String deviceFileName;

    /**
     * 设备字典
     */
    private  String dictName;

    private  Boolean  status;

    private  String deviceNo;

    private  Long createdTime;





}
