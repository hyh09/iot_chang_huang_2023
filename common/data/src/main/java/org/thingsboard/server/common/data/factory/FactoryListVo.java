package org.thingsboard.server.common.data.factory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
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
    @ApiModelProperty("未分配的设备")
    private List<Device> notDistributionList;

    public FactoryListVo(){}

    public FactoryListVo(List<Factory> factoryEntityList, List<Workshop> workshopEntityList, List<ProductionLine> productionLineEntityList, List<Device> deviceList) {
        this.factoryEntityList = factoryEntityList;
        this.workshopEntityList = workshopEntityList;
        this.productionLineEntityList = productionLineEntityList;
        this.deviceEntityList = deviceList;
        //处理反参中设备名称用name返回
        if(CollectionUtils.isNotEmpty(this.deviceEntityList)){
            deviceEntityList.forEach(i->{
                i.setName(i.getRename());
            });
        }
    }
    public void renameByNotDistributionList(List<Device> notDistributionDevice ){
        this.notDistributionList = notDistributionDevice;
        //处理反参中设备名称用name返回
        if(CollectionUtils.isNotEmpty(this.notDistributionList)){
            notDistributionList.forEach(i->{
                i.setName(i.getRename());
            });
        }
    }
    public void renameByDeviceEntityList(List<Device> deviceEntityList ){
        this.deviceEntityList = deviceEntityList;
        //处理反参中设备名称用name返回
        if(CollectionUtils.isNotEmpty(this.deviceEntityList)){
            deviceEntityList.forEach(i->{
                i.setName(i.getRename());
            });
        }
    }
}
