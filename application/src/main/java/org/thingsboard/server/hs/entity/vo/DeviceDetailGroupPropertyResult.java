package org.thingsboard.server.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 设备详情-分组属性
 *
 * @author wwj
 * @since 2021.10.26
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "设备详情-分组属性")
public class DeviceDetailGroupPropertyResult {

    /**
     * 分组属性id
     */
    @ApiModelProperty("分组属性id")
    private String id;

    /**
     * 分组属性名称
     */
    @ApiModelProperty("分组属性名称")
    private String name;

    /**
     * 分组属性描述
     */
    @ApiModelProperty("分组属性描述")
    private String title;

    /**
     * 分组属性数据
     */
    @ApiModelProperty("分组属性数据")
    private String data;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Long createdTime;
}
