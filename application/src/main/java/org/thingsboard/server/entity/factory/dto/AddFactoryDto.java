package org.thingsboard.server.entity.factory.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.entity.factory.AbstractFactory;

@Data
@ApiModel("AddFactoryDto")
public class AddFactoryDto extends AbstractFactory {

    public AddFactoryDto(){super();}

    public AddFactoryDto(Factory factory){super(factory);}
}
