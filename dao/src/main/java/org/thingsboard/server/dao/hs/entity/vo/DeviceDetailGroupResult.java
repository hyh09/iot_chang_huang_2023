package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 设备详情-分组
 *
 * @author wwj
 * @since 2021.10.26
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "设备详情-分组")
public class DeviceDetailGroupResult {

    /**
     * 分组id
     */
    @ApiModelProperty("分组id")
    private String id;

    /**
     * 分组名称
     */
    @ApiModelProperty("分组名称")
    private String name;

    /**
     * 分组属性列表
     */
    @ApiModelProperty("分组属性列表")
    List<DeviceDetailGroupPropertyResult> propertyResultList;
}
