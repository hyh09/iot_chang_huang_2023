package org.thingsboard.server.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "设备字典请求参数实体类")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictDeviceVO {
    @ApiModelProperty(value = "设备字典Id,null或空则为新增")
    private String id;

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

    @Valid
    @ApiModelProperty(value = "属性列表")
    private List<DictDevicePropertyVO> propertyList;

    @Valid
    @ApiModelProperty(value = "分组列表")
    private List<DictDeviceGroupVO> groupList;

    @Valid
    @ApiModelProperty(value = "部件列表")
    private List<DictDeviceComponentVO> componentList;
}