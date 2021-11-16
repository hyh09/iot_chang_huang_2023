package org.thingsboard.server.entity.workshop.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.thingsboard.server.common.data.workshop.Workshop;
import org.thingsboard.server.entity.workshop.AbstractWorkshop;

@Data
@ApiModel("WorkshopVo")
public class WorkshopVo extends AbstractWorkshop {

    public WorkshopVo(){super();}

    public WorkshopVo(Workshop workshop){super(workshop);}
}
