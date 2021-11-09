package org.thingsboard.server.entity.factory.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.factory.Factory;

@Data
@ApiModel(value = "FactoryVersionDto",description = "查询工厂最新版本")
public class FactoryVersionDto {

    @ApiModelProperty("所属工厂")
    private String factoryName;
    @ApiModelProperty("发布时间")
    private long publishTime;

    public Factory toFactory(){
        Factory factory = new Factory();
        factory.setName(this.factoryName);
        factory.setPublishTime(this.publishTime);
        return factory;
    }
}
