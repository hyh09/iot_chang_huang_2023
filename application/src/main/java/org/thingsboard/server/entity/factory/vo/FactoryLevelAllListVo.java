package org.thingsboard.server.entity.factory.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.factory.FactoryListVo;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.common.data.workshop.Workshop;
import org.thingsboard.server.entity.device.vo.DeviceVo;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("FactoryLevelAllListVo")
public class FactoryLevelAllListVo {

    @ApiModelProperty("工厂信息")
    private List<Factory> factoryList;
    @ApiModelProperty("车间信息")
    private List<Workshop> workshopList;
    @ApiModelProperty("产线信息")
    private List<ProductionLine> productionLineList;
    @ApiModelProperty("设备信息")
    private List<DeviceVo> deviceVoList;
    @ApiModelProperty("未分配的设备")
    private List<DeviceVo> notDistributionList;

    public FactoryLevelAllListVo(){}

    public FactoryLevelAllListVo(FactoryListVo factoryListVo){
        FactoryLevelAllListVo(factoryListVo.getFactoryEntityList(),
                factoryListVo.getWorkshopEntityList(),
                factoryListVo.getProductionLineEntityList(),
                factoryListVo.getDeviceEntityList(),
                factoryListVo.getNotDistributionList()
        );
    }

    public FactoryLevelAllListVo FactoryLevelAllListVo(List<Factory> factoryList,
                                                       List<Workshop> workshopList,
                                                       List<ProductionLine> productionLineList,
                                                       List<Device> deviceList,
                                                       List<Device> notDistributionList) {
        this.factoryList = factoryList;
        this.workshopList = workshopList;
        this.productionLineList = productionLineList;
        deviceVoList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(deviceList)){
            deviceList.forEach(i->{
                deviceVoList.add(new DeviceVo(i));
            });
        }
        this.notDistributionList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(notDistributionList)){
            notDistributionList.forEach(i->{
                this.notDistributionList.add(new DeviceVo(i));
            });
        }
        return this;
    }
}
