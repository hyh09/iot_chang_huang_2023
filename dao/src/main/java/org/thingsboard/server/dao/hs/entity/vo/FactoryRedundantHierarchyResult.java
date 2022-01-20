package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

/**
 * 工厂设备返回结果
 *
 * @author wwj
 * @since 2021.12.3
 */
@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@ApiModel(value = "工厂设备返回结果")
public class FactoryRedundantHierarchyResult {
    /**
     * 未分配设备列表
     */
    @ApiModelProperty(value = "未分配设备列表")
    private List<SimpleFactoryHierarchyResult> undistributedDevices;

    /**
     * 工厂列表
     */
    @ApiModelProperty("工厂列表")
    private List<SimpleFactoryHierarchyResult> factories;

    /**
     * 车间列表
     */
    @ApiModelProperty("车间列表")
    private List<SimpleFactoryHierarchyResult> workshops;

    /**
     * 产线列表
     */
    @ApiModelProperty("产线列表")
    private List<SimpleFactoryHierarchyResult> productionLines;

    /**
     * 设备列表
     */
    @ApiModelProperty("设备列表")
    private List<SimpleFactoryHierarchyResult> devices;

    public FactoryRedundantHierarchyResult() {
        super();
        this.undistributedDevices = Lists.newArrayList();
        this.factories = Lists.newArrayList();
        this.workshops = Lists.newArrayList();
        this.productionLines = Lists.newArrayList();
        this.devices = Lists.newArrayList();
    }
}
