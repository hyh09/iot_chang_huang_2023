package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.compress.utils.Lists;
import org.thingsboard.server.common.data.productionline.ProductionLine;
import org.thingsboard.server.common.data.workshop.Workshop;

import java.util.List;
import java.util.UUID;

/**
 * 车间返回结果
 *
 * @author wwj
 * @since 2021.12.3
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "车间返回结果")
public class SimpleWorkshopResult {
    /**
     * 车间Id
     */
    @ApiModelProperty("车间Id")
    private UUID id;

    /**
     * 车间名称
     */
    @ApiModelProperty("车间名称")
    private String name;

    /**
     * 产线列表
     */
    @ApiModelProperty("产线列表")
    private List<SimpleProductionLineResult> productionLines;

    public SimpleWorkshopResult(UUID id, String name) {
        this.id = id;
        this.name = name;
        this.productionLines = Lists.newArrayList();
    }

    public SimpleWorkshopResult(Workshop workshop) {
        this.id = workshop.getId();
        this.name = workshop.getName();
        this.productionLines = Lists.newArrayList();
    }
}
