package org.thingsboard.server.dao.hs.entity.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * 数据字典
 *
 * @author wwj
 * @since 2021.10.18
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "数据字典")
public class DictData extends BasePO {

    private static final long serialVersionUID = 4934987555236873700L;

    @ApiModelProperty(value = "数据字典Id")
    private String id;

    @ApiModelProperty(value = "租户Id")
    private String tenantId;

    @ApiModelProperty(value = "编码")
    private String code;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "单位")
    private String unit;

    @ApiModelProperty(value = "备注")
    private String comment;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "图片")
    private String picture;
}
