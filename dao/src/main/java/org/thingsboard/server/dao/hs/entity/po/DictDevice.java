package org.thingsboard.server.dao.hs.entity.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.thingsboard.server.dao.hs.dao.HsModelConstants;

import javax.persistence.Column;
import java.math.BigDecimal;

/**
 * 设备字典
 *
 * @author wwj
 * @since 2021.10.21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ApiModel(value = "设备字典")
public class DictDevice extends BasePO {

    private static final long serialVersionUID = 4934987555236873701L;
    /**
     * 设备字典Id
     */
    @ApiModelProperty(value = "设备字典Id")
    private String id;

    /**
     * 租户Id
     */
    @ApiModelProperty(value = "租户Id")
    private String tenantId;

    /**
     * 编码
     */
    @ApiModelProperty(value = "编码")
    private String code;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 类型
     */
    @ApiModelProperty(value = "类型")
    private String type;

    /**
     * 供应商
     */
    @ApiModelProperty(value = "供应商")
    private String supplier;

    /**
     * 型号
     */
    @ApiModelProperty(value = "型号")
    private String model;

    /**
     * 版本号
     */
    @ApiModelProperty(value = "版本号")
    private String version;

    /**
     * 保修期
     */
    @ApiModelProperty(value = "保修期")
    private String warrantyPeriod;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String comment;

    /**
     * 图标
     */
    @ApiModelProperty(value = "图标")
    private String icon;

    /**
     * 图片
     */
    @ApiModelProperty(value = "图片")
    private String picture;

    /**
     * 是否默认
     */
    @ApiModelProperty(value = "是否默认")
    private Boolean isDefault;

    /**
     * 是否核心
     */
    @ApiModelProperty(value = "是否核心")
    private Boolean isCore;

    /**
     * 额定产能
     */
    @ApiModelProperty(value = "额定产能")
    private BigDecimal ratedCapacity;
}
