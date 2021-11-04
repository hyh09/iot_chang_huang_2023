package org.thingsboard.server.dao.hs.entity.po;

import lombok.*;

/**
 * 设备字典-部件
 *
 * @author wwj
 * @since 2021.10.21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictDeviceComponent extends BasePO {

    private static final long serialVersionUID = 4934987555236873702L;
    /**
     * 设备字典-部件Id
     */
    private String id;

    /**
     * 设备字典Id
     */
    private String dictDeviceId;

    /**
     * 租户Id
     */
    private String tenantId;

    /**
     * 部件父Id
     */
    private String parentId;

    /**
     * 编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 类型
     */
    private String type;

    /**
     * 供应商
     */
    private String supplier;

    /**
     * 型号
     */
    private String model;

    /**
     * 版本号
     */
    private String version;

    /**
     * 保修期
     */
    private String warrantyPeriod;

    /**
     * 备注
     */
    private String comment;

    /**
     * 图标
     */
    private String icon;

    /**
     * 图片
     */
    private String picture;

    /**
     * 排序
     */
    private Integer sort;
}
