package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.compress.utils.Lists;
import org.thingsboard.server.common.data.factory.Factory;
import org.thingsboard.server.common.data.productionline.ProductionLine;

import java.util.List;
import java.util.UUID;

/**
 * 产线返回结果
 *
 * @author wwj
 * @since 2021.12.3
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "产线返回结果")
public class SimpleProductionLineResult {
    /**
     * 产线Id
     */
    @ApiModelProperty("产线Id")
    private UUID id;

    /**
     * 产线名称
     */
    @ApiModelProperty("产线名称")
    private String name;

    /**
     * 设备列表
     */
    @ApiModelProperty("设备列表")
    private List<SimpleDeviceResult> devices;

    public SimpleProductionLineResult(UUID id, String name) {
        this.id = id;
        this.name = name;
        this.devices = Lists.newArrayList();
    }

    public SimpleProductionLineResult(ProductionLine productionLine) {
        this.id = productionLine.getId();
        this.name = productionLine.getName();
        this.devices = Lists.newArrayList();
    }
}
