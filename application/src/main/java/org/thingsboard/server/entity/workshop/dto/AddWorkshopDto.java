package org.thingsboard.server.entity.workshop.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.thingsboard.server.common.data.workshop.Workshop;
import org.thingsboard.server.entity.workshop.AbstractWorkshop;

@Data
@ApiModel("AddWorkshopDto")
public class AddWorkshopDto extends AbstractWorkshop {

    public AddWorkshopDto(){super();}

    public AddWorkshopDto(Workshop workshop){super(workshop);}
}
