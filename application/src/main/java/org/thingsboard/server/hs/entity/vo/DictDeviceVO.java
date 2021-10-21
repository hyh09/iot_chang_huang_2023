package org.thingsboard.server.hs.entity.vo;

import com.google.gson.JsonObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.thingsboard.server.hs.entity.enums.DictDataTypeEnum;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "设备字典请求参数实体类")
public class DictDeviceVO {
    @ApiModelProperty(value = "设备字典Id,null或空则为新增")
    private String id;

    @ApiModelProperty(value = "编码")
    private String code;

    @NotNull
    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "类型")
    private DictDataTypeEnum type;

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
    private List<DictDevicePropertyQuery> propertyList;

    @Valid
    @ApiModelProperty(value = "分组列表")
    private List<DictDeviceGroupQuery> groupList;

    @Valid
    @ApiModelProperty(value = "部件列表")
    private List<DictDeviceComponentQuery> componentList;
}

@Data
@Accessors(chain = true)
@ApiModel(value = "设备字典属性实体类")
class DictDevicePropertyQuery {

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "内容")
    private String content;
}


@Data
@Accessors(chain = true)
@ApiModel(value = "设备字典分组属性实体类")
class DictDeviceGroupPropertyQuery {

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "内容")
    private String content;
}

@Data
@Accessors(chain = true)
@ApiModel(value = "设备字典分组实体类")
class DictDeviceGroupQuery {

    @ApiModelProperty(value = "名称")
    private String name;

    @Valid
    @ApiModelProperty(value = "分组属性列表")
    private List<DictDeviceGroupPropertyQuery> groupPropertyList;
}

@Data
@Accessors(chain = true)
@ApiModel(value = "设备字典部件实体类")
class DictDeviceComponentQuery {

    @ApiModelProperty(value = "编码")
    private String code;

    @NotNull
    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "类型")
    private DictDataTypeEnum type;

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

    @ApiModelProperty(value = "子部件Json数据, 为null则该部件已经没有子部件, 数据结构同此")
    private JsonObject jsonObject;
}