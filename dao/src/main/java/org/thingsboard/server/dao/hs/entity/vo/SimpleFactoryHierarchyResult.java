package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.thingsboard.server.dao.hs.entity.enums.FactoryHierarchyRowTypeEnum;

import java.util.UUID;

/**
 * 设备返回结果
 *
 * @author wwj
 * @since 2021.12.3
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "设备返回结果")
public class SimpleFactoryHierarchyResult {
    /**
     * 设备Id
     */
    @ApiModelProperty("设备Id")
    private UUID id;

    /**
     * 设备名称
     */
    @ApiModelProperty("设备名称")
    private String title;

    /**
     * 设备重命名名称
     */
    @ApiModelProperty("设备重命名名称")
    private String rename;

    /**
     * 设备Key
     */
    @ApiModelProperty("设备Key")
    private UUID key;

//    /**
//     * 父Id
//     */
//    @ApiModelProperty("父Id")
//    private UUID parentId;

    /**
     * 行类型
     */
    @ApiModelProperty("行类型")
    private FactoryHierarchyRowTypeEnum rowType;

    /**
     * 工厂Id
     */
    @ApiModelProperty("工厂Id")
    private UUID factoryId;
//
//    /**
//     * 工厂名称
//     */
//    @ApiModelProperty("工厂名称")
//    private String factoryName;

    /**
     * 车间Id
     */
    @ApiModelProperty("车间Id")
    private UUID workshopId;
//
//    /**
//     * 车间名称
//     */
//    @ApiModelProperty("车间名称")
//    private String workshopName;

    /**
     * 产线Id
     */
    @ApiModelProperty("产线Id")
    private UUID productionLineId;
//
//    /**
//     * 产线名称
//     */
//    @ApiModelProperty("产线名称")
//    private String productionLineName;

    /**
     * 排序值
     */
    @ApiModelProperty("排序值")
    private Integer sort;
}
