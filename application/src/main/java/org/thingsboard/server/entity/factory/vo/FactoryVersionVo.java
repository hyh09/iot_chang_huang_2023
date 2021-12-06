package org.thingsboard.server.entity.factory.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.factory.Factory;

@Data
@ApiModel(value = "FactoryVersionVo",description = "工厂最新版本")
public class FactoryVersionVo {

    @ApiModelProperty("工厂/网关版本")
    private String factoryVersion;
    @ApiModelProperty("所属工厂")
    private String factoryName;
    @ApiModelProperty("发布时间")
    private long publishTime;
    @ApiModelProperty("在线状态true-在线，false-离线")
    private Boolean active;
    @ApiModelProperty("网关设备名称")
    private String gatewayName;

    public FactoryVersionVo(){}

    public FactoryVersionVo(Factory factory){
        this.factoryVersion = factory.getFactoryVersion();
        this.factoryName = factory.getName();
        this.publishTime =factory.getPublishTime();
        this.gatewayName = factory.getGatewayName();
        this.active = factory.getActive();
    }

}
