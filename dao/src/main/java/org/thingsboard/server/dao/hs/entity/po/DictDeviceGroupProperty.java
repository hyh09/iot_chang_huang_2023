package org.thingsboard.server.dao.hs.entity.po;

import lombok.*;

/**
 * 设备字典-分组属性
 *
 * @author wwj
 * @since 2021.10.21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictDeviceGroupProperty extends BasePO {

    private static final long serialVersionUID = 4934987555236873705L;
    /**
     * 设备字典-分组属性Id
     */
    private String id;

    /**
     * 设备字典-分组Id
     */
    private String dictDeviceGroupId;

    /**
     * 设备字典Id
     */
    private String dictDeviceId;

    /**
     * 名称
     */
    private String name;

    /**
     * 内容
     */
    private String content;

    /**
     * 标题
     */
    private String title;
}
