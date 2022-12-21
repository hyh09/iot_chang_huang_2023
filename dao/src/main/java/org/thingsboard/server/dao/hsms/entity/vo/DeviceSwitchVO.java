package org.thingsboard.server.dao.hsms.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.thingsboard.server.dao.hs.entity.po.BasePO;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
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
public class DeviceSwitchVO extends BasePO {

    private static final long serialVersionUID = 4134987555236813704L;

    /**
     * 属性开关列表
     */
    @Valid
    @ApiModelProperty(value = "属性开关列表", required = true)
    private List<DictDevicePropertySwitchNewVO> propertySwitches;
}
