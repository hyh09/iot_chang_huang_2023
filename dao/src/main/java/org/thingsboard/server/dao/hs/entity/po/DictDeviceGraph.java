package org.thingsboard.server.dao.hs.entity.po;

import lombok.*;

import java.util.UUID;

/**
 * 设备字典-图表
 *
 * @author wwj
 * @since 2021.10.21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictDeviceGraph extends BasePO {

    private static final long serialVersionUID = 4134987555236873704L;
    /**
     * 设备字典-图表Id
     */
    private UUID id;

    /**
     * 设备字典Id
     */
    private UUID dictDeviceId;

    /**
     * 名称
     */
    private String name;

    /**
     * 是否显示图表
     */
    private Boolean enable;
}
