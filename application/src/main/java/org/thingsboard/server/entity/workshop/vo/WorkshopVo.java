package org.thingsboard.server.entity.workshop.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.workshop.Workshop;
import org.thingsboard.server.entity.workshop.AbstractWorkshop;

@Data
@ApiModel("WorkshopVo")
public class WorkshopVo extends AbstractWorkshop {

    @ApiModelProperty("工厂名称")
    private String factoryName;

    public WorkshopVo(){super();}

    public WorkshopVo(Workshop workshop){super(workshop);}
}
