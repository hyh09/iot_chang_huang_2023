package org.thingsboard.server.dao.hs.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.compress.utils.Lists;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.common.data.workshop.Workshop;
import org.thingsboard.server.dao.hs.entity.enums.FactoryHierarchyRowTypeEnum;
import org.thingsboard.server.dao.hs.entity.vo.FactoryHierarchyResult;
import org.thingsboard.server.dao.hs.entity.vo.FactoryRedundantHierarchyResult;
import org.thingsboard.server.dao.hs.entity.vo.SimpleFactoryHierarchyChild;
import org.thingsboard.server.dao.hs.entity.vo.SimpleFactoryHierarchyResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 工厂详情 BO
 *
 * @author wwj
 * @since 2021.10.26
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
public class FactoryDetailBO {
    List<Factory> factories;
    List<Workshop> workshops;
    List<ProductionLine> productionLines;
    List<Device> devices;

    public FactoryDetailBO() {
        super();
        this.factories = Lists.newArrayList();
        this.workshops = Lists.newArrayList();
        this.productionLines = Lists.newArrayList();
        this.devices = Lists.newArrayList();
    }

    public FactoryHierarchyResult toFactoryHierarchyResult() {
        FactoryHierarchyResult result = new FactoryHierarchyResult();
        var factoryMap = factories.stream().collect(Collectors.toMap(Factory::getId, SimpleFactoryHierarchyChild::new));
        var workshopMap = workshops.stream().collect(Collectors.toMap(Workshop::getId, SimpleFactoryHierarchyChild::new));
        var productionLineMap = productionLines.stream().collect(Collectors.toMap(ProductionLine::getId, SimpleFactoryHierarchyChild::new));

        if (!factories.isEmpty()) {
            factories.forEach(factory -> result.getResults().add(factoryMap.get(factory.getId())));
            workshops.forEach(workshop -> factoryMap.get(workshop.getFactoryId()).getChildren().add(workshopMap.get(workshop.getId())));
            productionLines.forEach(productionLine -> workshopMap.get(productionLine.getWorkshopId()).getChildren().add(productionLineMap.get(productionLine.getId())));
            devices.forEach(device -> {
                if (device.getProductionLineId() == null)
                    result.getUndistributedDevices().add(new SimpleFactoryHierarchyChild(device));
                else
                    productionLineMap.get(device.getProductionLineId()).getChildren().add(new SimpleFactoryHierarchyChild(device));
            });
        } else {
            devices.forEach(device -> result.getUndistributedDevices().add(new SimpleFactoryHierarchyChild(device)));
        }
        return result;
    }

    public FactoryRedundantHierarchyResult toFactoryRedundantHierarchyResult() {
        FactoryRedundantHierarchyResult result = new FactoryRedundantHierarchyResult();
        if (!factories.isEmpty()) {
            CompletableFuture.allOf(
                    CompletableFuture.runAsync(() -> result.setFactories(factories.stream().map(v -> SimpleFactoryHierarchyResult.builder()
                            .id(v.getId())
                            .title(v.getName())
                            .key(v.getId())
                            .rowType(FactoryHierarchyRowTypeEnum.FACTORY)
                            .build()).collect(Collectors.toList()))),
                    CompletableFuture.runAsync(() -> result.setWorkshops(workshops.stream().map(v -> SimpleFactoryHierarchyResult.builder()
                            .id(v.getId())
                            .title(v.getName())
                            .key(v.getId())
                            .rowType(FactoryHierarchyRowTypeEnum.WORKSHOP)
                            .factoryId(v.getFactoryId())
                            .build()).collect(Collectors.toList()))),
                    CompletableFuture.runAsync(() -> result.setProductionLines(productionLines.stream().map(v -> SimpleFactoryHierarchyResult.builder()
                            .id(v.getId())
                            .title(v.getName())
                            .key(v.getId())
                            .rowType(FactoryHierarchyRowTypeEnum.PRODUCTION_LINE)
                            .factoryId(v.getFactoryId())
                            .workshopId(v.getWorkshopId())
                            .build()).collect(Collectors.toList()))),
                    CompletableFuture.runAsync(() -> result.setDevices(devices.stream().filter(v -> v.getProductionLineId() != null).map(v -> SimpleFactoryHierarchyResult.builder()
                            .id(v.getId().getId())
                            .title(v.getName())
                            .key(v.getId().getId())
                            .rowType(FactoryHierarchyRowTypeEnum.DEVICE)
                            .factoryId(v.getFactoryId())
                            .workshopId(v.getWorkshopId())
                            .productionLineId(v.getProductionLineId())
                            .build()).collect(Collectors.toList()))),
                    CompletableFuture.runAsync(() -> result.setUndistributedDevices(devices.stream().filter(v -> v.getProductionLineId() == null).map(v -> SimpleFactoryHierarchyResult.builder()
                            .id(v.getId().getId())
                            .title(v.getName())
                            .key(v.getId().getId())
                            .rowType(FactoryHierarchyRowTypeEnum.DEVICE)
                            .factoryId(v.getFactoryId())
                            .workshopId(v.getWorkshopId())
                            .productionLineId(v.getProductionLineId())
                            .build()).collect(Collectors.toList())))
            ).join();
        } else {
            result.setDevices(devices.stream().map(v -> SimpleFactoryHierarchyResult.builder()
                    .id(v.getId().getId())
                    .title(v.getName())
                    .key(v.getId().getId())
                    .rowType(FactoryHierarchyRowTypeEnum.DEVICE)
                    .factoryId(v.getFactoryId())
                    .workshopId(v.getWorkshopId())
                    .productionLineId(v.getProductionLineId())
                    .build()).collect(Collectors.toList()));
        }
        return result;
    }
}
