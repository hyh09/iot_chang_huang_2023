package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.UUID;

/**
 * 工厂网关设备返回结果
 *
 * @author wwj
 * @since 2021.12.3
 */
@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@ApiModel(value = "工厂网关设备返回结果")
public class FactoryGatewayDevicesResult {
    /**
     * 工厂id
     */
    @ApiModelProperty(value = "工厂id")
    private UUID factoryId;

    /**
     * 网关设备Id列表
     */
    @ApiModelProperty(value = "网关设备Id列表")
    private List<UUID> gatewayDeviceIds;

    public FactoryGatewayDevicesResult() {
        super();
        this.gatewayDeviceIds = Lists.newArrayList();
    }
}
