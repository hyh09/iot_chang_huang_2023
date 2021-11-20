package org.thingsboard.server.dao.hs.entity.po;

import lombok.*;

/**
 * 设备字典-部件属性
 *
 * @author wwj
 * @since 2021.10.21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictDeviceComponentProperty extends BasePO {

    private static final long serialVersionUID = 4934987555236873705L;
    /**
     * 设备字典-分组属性Id
     */
    private String id;

    /**
     * 部件Id
     */
    private String componentId;

    /**
     * 设备字典Id
     */
    private String dictDeviceId;

    /**
     * 数据字典Id
     */
    private String dictDataId;

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

    /**
     * 排序
     */
    private Integer sort;
}
