package org.thingsboard.server.common.data.vo.resultvo.cap;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @program: springboot-jpa-20210106
 * @description: app的产能返回出参
 * @author: HU.YUNHUI
 * @create: 2021-11-09 10:55
 **/
@Data
@ToString
@ApiModel(value = "查询产能的出参实体")
public class ResultCapAppVo {
    /**
     * 总产能数据
     */
    @ApiModelProperty("总产能数据 Long类型 ")
    private String totalValue="0";

//    /**
//     * 默认是当前 00点
//     */
//    private Long startTime;
//    /**
//     * 默认是当天的 晚上24点数据
//     */
//    private Long endTime;
//
//    private String factoryId;
//    /**
//     * 所属工厂 默认是第一条数据的工厂 下的数据
//     */
//    private String factoryName;

    /**
     * 设备的具体数据
     */
    private List<AppDeviceCapVo> appDeviceCapVoList;


}
