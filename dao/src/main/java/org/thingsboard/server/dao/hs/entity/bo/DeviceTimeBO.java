package org.thingsboard.server.dao.hs.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.UUID;

/**
 * 设备关键参数时间 BO
 *
 * @author wwj
 * @since 2021.10.26
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class DeviceTimeBO {
    /**
     * 开机时间
     */
    private Long startingUpTime;

    /**
     * 停机时间
     */
    private Long shutdownTime;
}
