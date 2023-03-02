package org.thingsboard.server.dao.hs.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.dao.hs.entity.po.DictDevice;

import java.util.List;

/**
 * 设备配置实体类
 *
 * @author wwj
 * @since 2021.10.26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("设备配置")
public class DeviceProfileVO extends DeviceProfile{

    /**
     * 设备字典列表, 设备字典Id不能为空
     */
    @ApiModelProperty("设备字典Id列表")
    private List<String> dictDeviceIdList;
}
