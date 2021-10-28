package org.thingsboard.server.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.thingsboard.server.hs.entity.enums.DictDataDataTypeEnum;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
@ApiModel(value = "数据字典请求参数实体类")
public class DictDataQuery {
    @ApiModelProperty(value = "数据字典Id,null或空则为新增")
    private String id;

    @NotNull
    @ApiModelProperty(value = "编码")
    private String code;

    @NotNull
    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "类型")
    private DictDataDataTypeEnum type;

    @ApiModelProperty(value = "单位")
    private String unit;

    @ApiModelProperty(value = "备注")
    private String comment;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "图片")
    private String picture;
}
