package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
@ApiModel(value = "设备字典部件属性实体类")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictDeviceComponentPropertyVO {

    @ApiModelProperty(value = "id", notes = "新增或修改时均为null")
    private String id;

    @ApiModelProperty(value = "部件Id", notes = "新增或修改时均为null")
    private String componentId;

    @ApiModelProperty(value = "数据字典Id")
    private String dictDataId;

    /**
     * 遥测的 keyName
     */
    @NotNull
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 内容是 遥测数据
     */
    @NotNull
    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "创建时间", notes = "仅用于返参")
    private Long createdTime;

    @ApiModelProperty(value = "单位", notes = "仅用于遥测数据展示")
    private String unit;

    @ApiModelProperty(value = "图标", notes = "仅用于遥测数据展示")
    private String icon;
}
