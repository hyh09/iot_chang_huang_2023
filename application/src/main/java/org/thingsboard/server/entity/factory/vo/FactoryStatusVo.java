package org.thingsboard.server.entity.factory.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.factory.Factory;

import java.util.UUID;

@Data
@ApiModel(value = "FactoryStatusVo",description = "工厂状态")
public class FactoryStatusVo {

    @ApiModelProperty("工厂标识")
    public UUID id;

    @ApiModelProperty("工厂下网关的在线、离线状态。有一个在线视为正常，全部离线视为异常")
    private Boolean factoryStatus;

    public FactoryStatusVo(Factory factory){
        this.id = factory.getId();
        this.factoryStatus = factory.getFactoryStatus();
    }


}
