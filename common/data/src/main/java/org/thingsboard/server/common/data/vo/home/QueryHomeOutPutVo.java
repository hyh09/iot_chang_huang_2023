package org.thingsboard.server.common.data.vo.home;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 查询产能的入参实体
 * @author: HU.YUNHUI
 * @create: 2021-11-09 09:34
 **/
@Data
@ToString
@ApiModel(value = "首页-产量的接口入参")
public class QueryHomeOutPutVo {





    @ApiModelProperty("工厂id  UUID类型")
    private UUID factoryId;




}
