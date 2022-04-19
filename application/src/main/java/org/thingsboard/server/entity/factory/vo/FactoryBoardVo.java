package org.thingsboard.server.entity.factory.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.factory.Factory;

import java.util.UUID;

@Data
@ApiModel(value = "FactoryBoardVo",description = "集团看板")
public class FactoryBoardVo {

    @ApiModelProperty("工厂标识")
    public UUID id;

    @ApiModelProperty("经度")
    public String longitude;

    @ApiModelProperty("纬度")
    public String latitude;

    @ApiModelProperty("logo图标")
    public String logoIcon;

    @ApiModelProperty("工厂名称")
    public String name;

    public FactoryBoardVo(Factory factory){
        this.id = factory.getId();
        this.longitude = factory.getLongitude();
        this.latitude = factory.getLatitude();
        this.logoIcon = factory.getLogoIcon();
        this.name = factory.getName();
    }


}
