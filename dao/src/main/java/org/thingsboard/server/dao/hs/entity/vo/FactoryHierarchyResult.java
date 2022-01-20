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
public class FactoryHierarchyResult {
    /**
     * 未分配设备列表
     */
    @ApiModelProperty(value = "未分配设备列表")
    private List<SimpleFactoryHierarchyChild> undistributedDevices;

    /**
     * 层级列表
     */
    @ApiModelProperty("层级列表")
    private List<SimpleFactoryHierarchyChild> results;

    public FactoryHierarchyResult() {
        super();
        this.undistributedDevices = Lists.newArrayList();
        this.results = Lists.newArrayList();
    }
}
