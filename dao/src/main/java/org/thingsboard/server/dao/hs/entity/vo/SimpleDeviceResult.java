package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
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

    /**
     * 设备重命名名称
     */
    @ApiModelProperty("设备重命名名称")
    private String rename;

    public SimpleDeviceResult(UUID id, String name) {
        this.id = id;
        this.name = name;
        this.rename = name;
    }

    public SimpleDeviceResult(Device device) {
        this.id = device.getId().getId();
        this.name = device.getName();
        this.rename = device.getRename();
    }
}
