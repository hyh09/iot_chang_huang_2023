package org.thingsboard.server.common.data.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 看板的仪表盘的入参
 * @author: HU.YUNHUI
 * @create: 2022-03-08 09:06
 **/
@Data
@ApiModel(value = "看板的仪表盘的入参")
public class BoardV3DeviceDictionaryVo {

    /**
     * 设备字典
     */
    private UUID id;
    /**
     * 起始的时间
     */
    private  Long startTime;
    /**
     * 结束的时间
     */
    private  Long endTime;

}
