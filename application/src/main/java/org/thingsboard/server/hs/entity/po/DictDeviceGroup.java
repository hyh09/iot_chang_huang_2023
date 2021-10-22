package org.thingsboard.server.hs.entity.po;

import lombok.*;

/**
 * 设备字典-分组
 *
 * @author wwj
 * @since 2021.10.21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictDeviceGroup extends BasePO {

    private static final long serialVersionUID = 4934987555236873704L;
    /**
     * 设备字典-分组Id
     */
    private String id;

    /**
     * 设备字典Id
     */
    private String dictDeviceId;

    /**
     * 名称
     */
    private String name;
}
