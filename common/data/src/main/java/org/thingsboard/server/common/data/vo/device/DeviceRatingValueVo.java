package org.thingsboard.server.common.data.vo.device;

import lombok.Data;
import lombok.ToString;

import java.util.UUID;

/**
 * @program: thingsboard
 * @description: 设备对应的能耗的额定值
 * @author: HU.YUNHUI
 * @create: 2021-12-28 12:52
 **/
@Data
@ToString
public class DeviceRatingValueVo {

    /**
     * 设备的id
     */
    private UUID  id;

    private String content;


    public DeviceRatingValueVo(UUID id, String content) {
        this.id = id;
        this.content = content;
    }
}
