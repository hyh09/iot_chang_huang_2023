package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.thingsboard.server.dao.hs.entity.bo.Image;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "设备字典请求参数实体类")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictDeviceVO implements Image {
    @ApiModelProperty(value = "设备字典Id,null或空则为新增")
    private String id;

    @ApiModelProperty(value = "3d模型文件Id")
    private String fileId;

    @ApiModelProperty(value = "3d模型文件名", notes = "仅用于显示")
    private String fileName;

    @NotNull
    @ApiModelProperty(value = "编码", required = true)
    private String code;

    @NotNull
    @ApiModelProperty(value = "名称", required = true)
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

    @Valid
    @ApiModelProperty(value = "标准属性列表")
    private List<DictDeviceStandardPropertyVO> standardPropertyList;

    @NotNull
    @ApiModelProperty(value = "是否核心")
    private Boolean isCore;

    @Digits(integer = 19, fraction=2, message = "额定能耗格式不正确")
    @ApiModelProperty(value = "额定能耗")
    private BigDecimal ratedCapacity;
}