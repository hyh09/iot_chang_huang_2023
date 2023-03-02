package org.thingsboard.server.entity.factory.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.entity.factory.AbstractFactory;
@Data
@ApiModel("AddFactoryDto")
public class FactoryVo extends AbstractFactory {
    public FactoryVo(){super();}

    public FactoryVo(Factory factory){super(factory);}
}
