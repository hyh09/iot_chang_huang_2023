package org.thingsboard.server.dao.hsms.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.thingsboard.server.dao.hs.entity.enums.DictDevicePropertyTypeEnum;
import org.thingsboard.server.dao.hs.entity.po.BasePO;

import java.util.UUID;

/**
 * 设备参数筛选-设备详情
 *
 * @author wwj
 * @since 2021.10.21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictDevicePropertySwitchNewVO extends BasePO {

    private static final long serialVersionUID = 4134987555236813704L;

    /**
     * 开关数据库主键Id
     */
    @ApiModelProperty(value = "开关数据库主键Id", notes = "修改传；新增不传")
    private UUID id;

    /**
     * 设备Id
     */
    @ApiModelProperty(value = "设备Id", required = true)
    private UUID deviceId;

    /**
     * 设备字典Id
     */
    @ApiModelProperty(value = "设备字典Id", required = true)
    private UUID dictDeviceId;

    /**
     * 属性Id
     */
    @ApiModelProperty(value = "属性Id", required = true)
    private UUID propertyId;

    /**
     * 属性类型
     */
    @ApiModelProperty(value = "属性类型", required = true)
    private DictDevicePropertyTypeEnum propertyType;

    /**
     * 属性名称
     */
    @ApiModelProperty(value = "属性名称", notes = "仅用于展示")
    private String propertyName;

    /**
     * 属性标题
     */
    @ApiModelProperty(value = "属性标题", notes = "仅用于展示")
    private String propertyTitle;

    /**
     * 属性单位
     */
    @ApiModelProperty(value = "属性单位", notes = "仅用于展示")
    private String propertyUnit;

    /**
     * 属性内容
     */
    @ApiModelProperty(value = "属性内容", notes = "仅用于展示")
    private String propertyContent;

    /**
     * 属性开关
     */
    @ApiModelProperty(value = "属性开关", required = true)
    private Boolean propertySwitch;
}
