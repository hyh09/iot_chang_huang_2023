package org.thingsboard.server.entity.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SystemVo",description = "系统版本")
public class SystemVo {
    @ApiModelProperty("工厂/网关版本")
    private String version;

    @ApiModelProperty("发布时间")
    private long publishTime;

    public SystemVo(String version,long publishTime){
        this.version = version;
        this.publishTime = publishTime;
    }
}
