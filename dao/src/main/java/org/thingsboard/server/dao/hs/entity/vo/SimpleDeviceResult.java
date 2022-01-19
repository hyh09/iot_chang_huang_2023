package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.thingsboard.server.common.data.Device;

import java.util.UUID;

/**
 * 设备返回结果
 *
 * @author wwj
 * @since 2021.12.3
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@ApiModel(value = "设备返回结果")
public class SimpleDeviceResult {
    /**
     * 设备Id
     */
    @ApiModelProperty("设备Id")
    private UUID id;

    /**
     * 设备名称
     */
    @ApiModelProperty("设备名称")
    private String name;

    public SimpleDeviceResult(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public SimpleDeviceResult(Device device) {
        this.id = device.getId().getId();
        this.name = device.getName();
    }
}
