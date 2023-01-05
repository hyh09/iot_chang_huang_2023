package org.thingsboard.server.dao.hsms.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
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
public class DeviceHutBO implements Serializable {

    /**
     * id
     */
    private UUID id;

    /**
     * 开始时长
     */
    private Long startTime;

    /**
     * 总时长
     */
    private Long totalTime;
}
