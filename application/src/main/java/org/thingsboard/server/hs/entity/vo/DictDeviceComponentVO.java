package org.thingsboard.server.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.thingsboard.server.hs.entity.enums.DictDataTypeEnum;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "设备字典部件实体类")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictDeviceComponentVO {

    @ApiModelProperty(value = "主键id, 新增或修改时均为null")
    private String id;

    @ApiModelProperty(value = "父id, 新增或修改时均为null")
    private String parentId;

    @ApiModelProperty(value = "设备字典Id")
    private String dictDeviceId;

    @NotNull
    @ApiModelProperty(value = "编码")
    private String code;

    @NotNull
    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "供应商")
    private String supplier;

    @ApiModelProperty(value = "型号")
    private String model;

    @ApiModelProperty(value = "版本号")
    private String version;

    @ApiModelProperty(value = "保修期")
    private String warrantyPeriod;

    @ApiModelProperty(value = "备注")
    private String comment;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "图片")
    private String picture;

    @ApiModelProperty(value = "子部件列表数据, 为null则该部件已经没有子部件, 数据结构同部件")
    private List<DictDeviceComponentVO> componentList;
}
