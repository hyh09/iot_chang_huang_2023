package org.thingsboard.server.common.data.factory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.common.data.workshop.Workshop;

import java.util.List;

@Data
@ApiModel("FactoryList")
public class FactoryListVo {

    @ApiModelProperty("工厂信息")
    private List<Factory> factoryEntityList ;
    @ApiModelProperty("车间信息")
    private List<Workshop> workshopEntityList ;
    @ApiModelProperty("产线信息")
    private List<ProductionLine> productionLineEntityList ;
    @ApiModelProperty("设备信息")
    private List<Device> deviceEntityList ;

    public FactoryListVo(){}

    public FactoryListVo(List<Factory> factoryEntityList, List<Workshop> workshopEntityList, List<ProductionLine> productionLineEntityList, List<Device> deviceList) {
        this.factoryEntityList = factoryEntityList;
        this.workshopEntityList = workshopEntityList;
        this.productionLineEntityList = productionLineEntityList;
        this.deviceEntityList = deviceList;
    }
}
