package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.compress.utils.Lists;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.common.data.workshop.Workshop;

import java.util.List;
import java.util.UUID;

/**
 * 工厂层级结果子集
 *
 * @author wwj
 * @since 2021.12.3
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "工厂层级结果子集")
public class SimpleFactoryHierarchyChild {
    /**
     * Id
     */
    @ApiModelProperty("Id")
    private UUID key;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String title;

    /**
     * 是否是叶子节点
     */
    @ApiModelProperty("是否是叶子节点")
    private Boolean isLeaf;

    /**
     * 子集
     */
    @ApiModelProperty("子集")
    private List<SimpleFactoryHierarchyChild> children;

    public SimpleFactoryHierarchyChild(UUID key, String title) {
        this.key = key;
        this.title = title;
        this.isLeaf = false;
        this.children = Lists.newArrayList();
    }

    public SimpleFactoryHierarchyChild(UUID key, String title, Boolean isLeaf) {
        this.key = key;
        this.title = title;
        this.isLeaf = isLeaf;
        this.children = Lists.newArrayList();
    }

    public SimpleFactoryHierarchyChild(Factory factory) {
        this.key = factory.getId();
        this.title = factory.getName();
        this.isLeaf = false;
        this.children = Lists.newArrayList();
    }

    public SimpleFactoryHierarchyChild(Workshop workshop) {
        this.key = workshop.getId();
        this.title = workshop.getName();
        this.isLeaf = false;
        this.children = Lists.newArrayList();
    }

    public SimpleFactoryHierarchyChild(ProductionLine productionLine) {
        this.key = productionLine.getId();
        this.title = productionLine.getName();
        this.isLeaf = false;
        this.children = Lists.newArrayList();
    }

    public SimpleFactoryHierarchyChild(Device device) {
        this.key = device.getId().getId();
        this.title = device.getName();
        this.isLeaf = true;
        this.children = Lists.newArrayList();
    }
}
